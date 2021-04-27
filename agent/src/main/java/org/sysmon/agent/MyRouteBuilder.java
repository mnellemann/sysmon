package org.sysmon.agent;

import org.apache.camel.builder.RouteBuilder;
import org.sysmon.agent.beans.ProcessorBeanAix;
import org.sysmon.agent.beans.ProcessorBeanLinux;

public class MyRouteBuilder extends RouteBuilder {

    private static final String osName = System.getProperty("os.name").toLowerCase();

    @Override
    public void configure() throws Exception {


        // TODO: Some smarter way to do this ?

        if(osName.contains("linux")) {
            // Linux specific beans
            from("timer:collect?period=5000")
                    .bean(new ProcessorBeanLinux(), "getMetrics")
                    .to("seda:metrics");

        } else if(osName.contains("aix")) {
            // AIX specific beans
            from("timer:collect?period=5000")
                    .bean(new ProcessorBeanAix(), "getMetrics")
                    .to("seda:metrics");

        } else {
            // Unsupported OS
            throw new UnsupportedOperationException("OS not implemented: " + osName);
        }



        // TODO: Discover beans on classpath and setup accordingly ??

        // Setup metrics measurement beans
        from("timer:collect?period=5000")
            .bean("memoryBean", "getMetrics")
            .to("seda:metrics");

        from("timer:collect?period=5000")
            .bean("diskBean", "getMetrics")
            .to("seda:metrics");


        // TODO: Somehow combine all results in a format suitable for sending to a central REST endpoint

        from("seda:metrics").process("metricProcessor");


    }

}

