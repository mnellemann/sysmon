package org.sysmon.collector.bean;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Hello implements Processor {

    private final static Logger log = LoggerFactory.getLogger(Hello.class);

    public void process(Exchange exchange) throws Exception {
        String name = exchange.getIn().getHeader("name", String.class);
        String msg = "Hello " + Objects.requireNonNull(name, "universe");
        log.info(msg);

        exchange.getMessage().setBody(msg);
    }
}