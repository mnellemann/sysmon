package sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import sysmon.shared.Measurement;
import sysmon.shared.MetricResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricResultToPointProcessor implements Processor {

    private static String influxDbName;

    MetricResultToPointProcessor(String influxDbName) {
        MetricResultToPointProcessor.influxDbName = influxDbName;
    }

    @Override
    public void process(Exchange exchange) {

        MetricResult metricResult = exchange.getIn().getBody(MetricResult.class);
        List<Measurement> measurementList = metricResult.getMeasurements();

        //log.info("Size of measurements: " + measurementList.size());

        BatchPoints.Builder batchPoints = BatchPoints
            .database(MetricResultToPointProcessor.influxDbName)
            .precision(TimeUnit.SECONDS)
            .tag("hostname", metricResult.getHostname());

        for(Measurement measurement : measurementList) {

            Point.Builder point = Point.measurement(metricResult.getName())
                .time(metricResult.getTimestamp(), TimeUnit.SECONDS)
                .fields(measurement.getFields())
                .tag(measurement.getTags());

            batchPoints.point(point.build());

        }

        exchange.getIn().setBody(batchPoints.build());

    }

}
