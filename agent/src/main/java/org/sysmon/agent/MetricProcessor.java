package org.sysmon.agent;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricResult;
import org.sysmon.shared.dto.MetricMessageDTO;

import java.util.concurrent.atomic.AtomicLong;

public class MetricProcessor implements Processor {

    private final static Logger log = LoggerFactory.getLogger(MetricProcessor.class);

    private static final AtomicLong counter = new AtomicLong();

    public void process(Exchange exchange) throws Exception {

        MetricResult reading = exchange.getIn().getBody(MetricResult.class);
        log.debug(reading.toString());

        // do something with the payload and/or exchange here
        //exchange.getIn().setBody("Changed body");

        // do something...
        MetricMessageDTO payload = new MetricMessageDTO("event " + reading, counter.getAndIncrement());
        exchange.getIn().setBody(payload, MetricMessageDTO.class);
    }


}