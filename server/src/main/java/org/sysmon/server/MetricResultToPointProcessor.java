package org.sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.Point;
import org.sysmon.shared.MeasurementPair;
import org.sysmon.shared.MetricResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MetricResultToPointProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        MetricResult metricResult = exchange.getIn().getBody(MetricResult.class);

        Point.Builder builder = Point.measurement(metricResult.getName())
                .time(metricResult.getTimestamp(), TimeUnit.MILLISECONDS)
                .tag("hostname", metricResult.getHostname());

        List<MeasurementPair> measurements = metricResult.getMeasurements();
        for(MeasurementPair measurement : measurements) {
            if(measurement.getValue() instanceof Number) {
                Number num = (Number) measurement.getValue();
                builder.addField(measurement.getName(), num);
            } else if(measurement.getValue() instanceof Boolean) {
                Boolean bol = (Boolean) measurement.getValue();
                builder.addField(measurement.getName(), bol);
            } else {
                String str = (String) measurement.getValue();
                builder.addField(measurement.getName(), str);
            }
        }

        exchange.getIn().setBody(builder.build());
    }

}
