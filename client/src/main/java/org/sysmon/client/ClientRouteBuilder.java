package org.sysmon.client;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.Registry;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricResult;

import java.util.List;

public class ClientRouteBuilder extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClientRouteBuilder.class);

    @Override
    public void configure() throws Exception {

        Registry registry = getContext().getRegistry();

        PluginManager pluginManager = new JarPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<MetricExtension> metricExtensions = pluginManager.getExtensions(MetricExtension.class);
        //log.info(String.format("Found %d extensions for extension point '%s':", metricExtensions.size(), MetricExtension.class.getName()));
        for (MetricExtension ext : metricExtensions) {
            if(ext.isSupported()) {
                log.info(">>> Enabling extension: " + ext.getDescription());

                // Setup Camel route for this extension
                from("timer:collect?period=15000")
                        .bean(ext, "getMetrics")
                        //.doTry()
                        .process(new MetricEnrichProcessor(registry))
                        .choice().when(exchangeProperty("skip").isEqualTo(true))
                            .log("Skipping empty: ${body}")
                            .stop()
                        .otherwise()
                            .to("seda:metrics");

            }
        }


        from("seda:metrics")
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