package org.sysmon.collector.bean;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricResult;
import org.sysmon.shared.dto.MetricMessageDTO;

public class IncomingMetricProcessor implements Processor {

    private final static Logger log = LoggerFactory.getLogger(IncomingMetricProcessor.class);

    public void process(Exchange exchange) throws Exception {

        MetricResult payload = exchange.getIn().getBody(MetricResult.class);
        //log.info("I am going to send this data to InfluxDB.");
        //log.info(payload.toString());

        exchange.getMessage().setBody("OK");
    }


}
