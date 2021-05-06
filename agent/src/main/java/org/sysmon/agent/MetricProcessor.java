package org.sysmon.agent;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.sysmon.shared.MetricResult;


public class MetricProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {
        MetricResult result = exchange.getIn().getBody(MetricResult.class);
        if(result.getMeasurementList().size() < 1) {
            exchange.setProperty("skip", true);
        }

        exchange.getIn().setHeader("component", result.getName());

        // TODO: Read hostname from configuration
        result.setHostname("sauron");

        exchange.getIn().setBody(result);
    }

}