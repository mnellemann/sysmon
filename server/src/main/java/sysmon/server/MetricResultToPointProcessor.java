package sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricResultToPointProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(MetricResultToPointProcessor.class);
    private static String influxDbName;

    MetricResultToPointProcessor(String influxDbName) {
        MetricResultToPointProcessor.influxDbName = influxDbName;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        MetricResult metricResult = exchange.getIn().getBody(MetricResult.class);
        List<Measurement> measurementList = metricResult.getMeasurements();

        //log.info("Size of measurements: " + measurementList.size());

        BatchPoints.Builder batchPoints = BatchPoints
            .database(MetricResultToPointProcessor.influxDbName)
            .precision(TimeUnit.MILLISECONDS)
            .tag("hostname", metricResult.getHostname());

        for(Measurement measurement : measurementList) {

            Point.Builder point = Point.measurement(metricResult.getName())
                .time(metricResult.getTimestamp(), TimeUnit.MILLISECONDS);

            for (Map.Entry<String,String> entry : measurement.getTags().entrySet()) {
                //log.info("process() - tag: " + entry.getKey() + "=" + entry.getValue());
                point.tag(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String,Object> entry : measurement.getFields().entrySet()) {
                //log.info("process() - field: " + entry.getKey() + "=" + entry.getValue());
                if(entry.getValue() instanceof Number) {
                    Number num = (Number) entry.getValue();
                    point.addField(entry.getKey(), num);
                } else if(entry.getValue() instanceof Boolean) {
                    Boolean bol = (Boolean) entry.getValue();
                    point.addField(entry.getKey(), bol);
                } else {
                    String str = (String) entry.getValue();
                    point.addField(entry.getKey(), str);
                }
            }
            batchPoints.point(point.build());
        }

        exchange.getIn().setBody(batchPoints.build());

    }

}
