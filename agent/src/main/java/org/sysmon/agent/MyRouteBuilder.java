package org.sysmon.agent;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricExtension;

import java.util.List;

public class MyRouteBuilder extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(MyRouteBuilder.class);


    @Override
    public void configure() throws Exception {

        PluginManager pluginManager = new JarPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<MetricExtension> metricExtensions = pluginManager.getExtensions(MetricExtension.class);
        //log.info(String.format("Found %d extensions for extension point '%s':", metricExtensions.size(), MetricExtension.class.getName()));
        for (MetricExtension ext : metricExtensions) {
            if(ext.isSupported()) {
                log.info(">>> Enabling extension: " + ext.getDescription());

                // Setup Camel route for this extension
                from("timer:collect?period=5000")
                        .bean(ext, "getMetrics")
                        .setHeader("ext", constant(ext.getName()))
                        .to("seda:metrics");

            }
        }



        /*
         TODO:  How to combine/wrap the individual metrics into a container which also contains
                some extra information, such as our hostname, operating system, timestamp, etc.

           Like one JSON or XML output with all metrics:

        {
           "hostname": "sauron",
           "timestamp": "1322334343434",
           "metrics": [
             { "processor": [
                { "cpu0":"10" },
                { "cpu1":"12" }
             ]},
             { "memory": [
                { "memUsed": "323434"},
                { "memFree": "4454545"}
            ]}
          ]
        }
        */


        /*
        from("seda:metrics")
                .aggregate(header("ext"), new ArrayListAggregationStrategy())
                .completionInterval(5000)
                .to("seda:aggregated");

        from("seda:aggregated")
                .log("${body}");
        */


        // Send to collector when combined
        from("seda:metrics")
                .process(new MetricProcessor())
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .to("http://127.0.0.1:9925/metrics");




    }

}