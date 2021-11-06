package sysmon.client;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.spi.Registry;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.ComboResult;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientRouteBuilder extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClientRouteBuilder.class);

    @Override
    public void configure() {

        Registry registry = getContext().getRegistry();
        Configuration configuration = (Configuration) registry.lookupByName("configuration");

        Path[] pluginpaths = { Paths.get(registry.lookupByNameAndType("pluginPath", String.class)) };
        PluginManager pluginManager = new JarPluginManager(pluginpaths);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<String> providers = new ArrayList<>();
        List<MetricExtension> metricExtensions = pluginManager.getExtensions(MetricExtension.class);
        for (MetricExtension ext : metricExtensions) {

            final String name = ext.getName();
            final String provides = ext.getProvides();

            // Load configuration if available
            if(configuration.isForExtension(name)) {
                log.info("Loading configuring for extension: " + ext.getDescription());
                ext.setConfiguration(configuration.getForExtension(name));
            }

            if(ext.isSupported() && ext.isEnabled()) {

                // Check that another extension has not already been loaded - TODO: Is this required ?
                if(providers.contains(provides)) {
                    log.warn("Skipping extension (already provided): " + ext.getName());
                    continue;
                }

                log.info("Enabling extension: " + ext.getDescription());
                providers.add(provides);

                // Setup Camel route for this extension
                // a unique timer name gives the timer it's own thread, otherwise it's a shared thread for other timers with same name.
                String timerName = ext.isThreaded() ? ext.getProvides() : "default";
                from("timer:"+timerName+"?fixedRate=true&period=30s")
                        .bean(ext, "getMetrics")
                        .outputType(MetricResult.class)
                        .process(new MetricEnrichProcessor(registry))
                        .choice().when(exchangeProperty("skip").isEqualTo(true))
                            .log(LoggingLevel.WARN,"Skipping empty measurement.")
                            .stop()
                        .otherwise()
                            .log("${body}")
                            .to("seda:metrics?discardWhenFull=true");
            } else {
                log.info("Skipping extension (not supported or disabled): " + ext.getDescription());
            }

        }

        from("seda:metrics?purgeWhenStopping=true")
                .aggregate(constant(true), AggregationStrategies.beanAllowNull(ComboAppender.class, "append"))
                .completionTimeout(5000L)
                .doTry()
                    .to("seda:outbound?discardWhenFull=true")
                    .log("Aggregating ${body} before sending to server.")
                .doCatch(Exception.class)
                    .log(LoggingLevel.WARN, "Error: ${exception.message}.")
                .end();

        from("seda:outbound?purgeWhenStopping=true")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .doTry()
                    .marshal(new JacksonDataFormat(ComboResult.class))
                    .to((String)registry.lookupByName("myServerUrl"))
                    .log("${body}")
                .doCatch(Exception.class)
                    .log(LoggingLevel.WARN,"Error: ${exception.message}.")
                .end();

    }


}
