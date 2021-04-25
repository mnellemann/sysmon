package org.sysmon.agent;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricResult;

public class MetricProcessor implements Processor {

    private final static Logger log = LoggerFactory.getLogger(MetricProcessor.class);


    public void process(Exchange exchange) throws Exception {

        MetricResult payload = exchange.getIn().getBody(MetricResult.class);
        log.info(payload.toString());

        // do something with the payload and/or exchange here
        //exchange.getIn().setBody("Changed body");

        // do something...
    }


}
