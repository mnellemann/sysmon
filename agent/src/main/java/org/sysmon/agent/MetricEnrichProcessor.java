package org.sysmon.agent;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.sysmon.shared.MetricResult;


public class MetricEnrichProcessor implements Processor {

    // TODO: Read hostname from future configuration
    private final static String hostname = "saruman";

    public void process(Exchange exchange) throws Exception {
        MetricResult result = exchange.getIn().getBody(MetricResult.class);
        result.setHostname(hostname);

        // We make sure MetricResults with no measurements are not sent further down the line
        if(result.getMeasurements().size() < 1) {
            exchange.setProperty("skip", true);
        }

        exchange.getIn().setHeader("component", result.getName());
        exchange.getIn().setBody(result);
    }

}