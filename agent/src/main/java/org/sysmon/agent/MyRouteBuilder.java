package org.sysmon.agent;

import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {

        // Setup metrics measurement beans
        // TODO: Discover beans on classpath and setup accordingly
        from("timer:collect?period=5000")
            .bean("memoryBean", "getMetrics")
            .to("seda:metrics");
        from("timer:collect?period=5000")
            .bean("processorBean", "getMetrics")
            .to("seda:metrics");
        from("timer:collect?period=5000")
            .bean("diskBean", "getMetrics")
            .to("seda:metrics");

        // TODO: Somehow combine all results in a format suitable for sending to REST endpoint
        from("seda:metrics").process("metricProcessor");

        // Could we store the last n results from each bean, and send mean value to the REST endpoint?


    }

}

