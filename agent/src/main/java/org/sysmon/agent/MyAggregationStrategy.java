package org.sysmon.agent;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.sysmon.shared.MetricResult;

public class MyAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Message newIn = newExchange.getIn();
        MetricResult oldBody = oldExchange.getIn().getBody(MetricResult.class);
        String newBody = newIn.getBody(String.class);
        newIn.setBody(oldBody + newBody);
        return newExchange;
    }

}
