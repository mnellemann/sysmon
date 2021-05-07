package org.sysmon.server.bean;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.sysmon.shared.MetricResult;

public class IncomingMetricProcessor implements Processor {

    public void process(Exchange exchange) throws Exception {

        MetricResult payload = exchange.getIn().getBody(MetricResult.class);
        //log.info("I am going to send this data to InfluxDB.");
        //log.info(payload.toString());

        exchange.getMessage().setBody("OK");
    }


}
