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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClientRouteBuilder extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClientRouteBuilder.class);

    @Override
    public void configure() throws Exception {

        Registry registry = getContext().getRegistry();

        Path[] pluginpaths = { new File("/opt/sysmon/plugins").toPath() };

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

                // Setup Camel route for this extension
                from("timer:collect?fixedRate=true&period=30s")
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
