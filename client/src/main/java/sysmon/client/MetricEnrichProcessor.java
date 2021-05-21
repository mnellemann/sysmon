package sysmon.client;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.Registry;
import sysmon.shared.MetricResult;


public class MetricEnrichProcessor implements Processor {

    private final Registry registry;

    public MetricEnrichProcessor(Registry registry) {
        this.registry = registry;
    }


    public void process(Exchange exchange) throws Exception {

        MetricResult metricResult = exchange.getIn().getBody(MetricResult.class);

        // We make sure MetricResults with no measurements are not sent further down the line
        if(metricResult == null || metricResult.getMeasurement() == null) {
            exchange.setProperty("skip", true);
            return;
        }

        metricResult.setHostname((String)registry.lookupByName("myHostname"));

        exchange.getIn().setHeader("hostname", metricResult.getHostname());
        exchange.getIn().setHeader("metric", metricResult.getName());
        exchange.getIn().setBody(metricResult);
    }

}