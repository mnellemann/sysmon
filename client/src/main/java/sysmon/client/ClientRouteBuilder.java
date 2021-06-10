package sysmon.client;

import org.apache.camel.Exchange;
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

        Path[] pluginpaths = { Paths.get(registry.lookupByNameAndType("pluginPath", String.class)) };
        PluginManager pluginManager = new JarPluginManager(pluginpaths);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<String> providers = new ArrayList<>();
        List<MetricExtension> metricExtensions = pluginManager.getExtensions(MetricExtension.class);
        for (MetricExtension ext : metricExtensions) {

            if(ext.isSupported()) {

                String provides = ext.getProvides();
                if(providers.contains(provides)) {
                    log.warn("Skipping extension (already provided): " + ext.getName());
                    continue;
                }

                log.info(">>> Enabling extension: " + ext.getDescription());
                providers.add(provides);


                // TODO: Make timer thread configurable

                // Setup Camel route for this extension
                // a unique timer name gives the timer it's own thread, otherwise it's a shared thread for other timers with same name.
                //from("timer:"+provides+"?fixedRate=true&period=30s")
                from("timer:extensions?fixedRate=true&period=30s")
                        .bean(ext, "getMetrics")
                        //.doTry()
                        .process(new MetricEnrichProcessor(registry))
                        .choice().when(exchangeProperty("skip").isEqualTo(true))
                            .log("Skipping empty measurement.")
                            .stop()
                        .otherwise()
                            .to("seda:metrics?discardWhenFull=true");
            } else {
                log.info(">>> Skipping extension (not supported here): " + ext.getDescription());
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
                    .log("Error: ${exception.message}")
                    //.log("Error sending metric to collector: ${body}")
                .end();

    }

}
