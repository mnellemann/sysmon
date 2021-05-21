package sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricResult;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricResultToPointProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(MetricResultToPointProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        MetricResult metricResult = exchange.getIn().getBody(MetricResult.class);
        Measurement measurement = metricResult.getMeasurement();

        Point.Builder builder = Point.measurement(metricResult.getName())
                .time(metricResult.getTimestamp(), TimeUnit.MILLISECONDS)
                .tag("hostname", metricResult.getHostname());

        for (Map.Entry<String,String> entry : measurement.getTags().entrySet()) {
            log.debug("process() - tag: " + entry.getKey() + "=" + entry.getValue());
            builder.tag(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String,Object> entry : measurement.getFields().entrySet()) {
            log.debug("process() - field: " + entry.getKey() + "=" + entry.getValue());
            if(entry.getValue() instanceof Number) {
                Number num = (Number) entry.getValue();
                builder.addField(entry.getKey(), num);
            } else if(entry.getValue() instanceof Boolean) {
                Boolean bol = (Boolean) entry.getValue();
                builder.addField(entry.getKey(), bol);
            } else {
                String str = (String) entry.getValue();
                builder.addField(entry.getKey(), str);
            }
        }

        exchange.getIn().setBody(builder.build());

    }

}