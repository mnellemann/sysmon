package sysmon.client;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;
import sysmon.shared.MetricResult;

public class ListOfResultsStrategy extends AbstractListAggregationStrategy<MetricResult> {

    @Override
    public MetricResult getValue(Exchange exchange) {
        return exchange.getIn().getBody(MetricResult.class);
    }
}