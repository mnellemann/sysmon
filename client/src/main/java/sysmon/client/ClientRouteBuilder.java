package sysmon.client;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.Registry;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                log.info(">>> Loading configuring for extension: " + ext.getDescription());
                ext.setConfiguration(configuration.getForExtension(name));
            }

            if(ext.isSupported() && ext.isEnabled()) {

                // Check that another extension has not already been loaded - TODO: Is this required ?
                if(providers.contains(provides)) {
                    log.warn("Skipping extension (already provided): " + ext.getName());
                    continue;
                }

                log.info(">>> Enabling extension: " + ext.getDescription());
                providers.add(provides);

                // Setup Camel route for this extension
                // a unique timer name gives the timer it's own thread, otherwise it's a shared thread for other timers with same name.
                //from("timer:extensions?fixedRate=true&period=30s")
                from("timer:"+provides+"?fixedRate=true&period=30s")
                        .bean(ext, "getMetrics")
                        //.doTry()
                        .outputType(MetricResult.class)
                        .process(new MetricEnrichProcessor(registry))
                        .choice().when(exchangeProperty("skip").isEqualTo(true))
                            .log(LoggingLevel.WARN,"Skipping empty measurement.")
                            .stop()
                        .otherwise()
                            .to("seda:metrics?discardWhenFull=true");
            } else {
                log.info(">>> Skipping extension (not supported or disabled): " + ext.getDescription());
            }

        }


        // TODO: Make 'concurrentConsumers' configurable
        from("seda:metrics?concurrentConsumers=1")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                //.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .doTry()
                    //.process(new MetricProcessor())
                    .marshal().json(JsonLibrary.Jackson, MetricResult.class)
                    .to((String)registry.lookupByName("myServerUrl"))
                .doCatch(Exception.class)
                    .log(LoggingLevel.WARN,"Error: ${exception.message}")
                    //.log("Error sending metric to collector: ${body}")
                .end();

    }


}
