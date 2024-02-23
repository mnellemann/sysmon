package sysmon.server;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import sysmon.shared.Measurement;
import sysmon.shared.MetricResult;

public class MetricResultToPointProcessor implements Processor {

    private static String influxDbName;
    private boolean localTime = false;


    MetricResultToPointProcessor(String influxDbName) {
        MetricResultToPointProcessor.influxDbName = influxDbName;
    }


    MetricResultToPointProcessor(String influxDbName, boolean localTime) {
        MetricResultToPointProcessor.influxDbName = influxDbName;
        this.localTime = localTime;
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
                .fields(measurement.getFields())
                .tag(measurement.getTags());

            // Override timestamp from client
            if(localTime) {
                point.time(Instant.now().getEpochSecond(), TimeUnit.SECONDS);
            } else {
                point.time(metricResult.getTimestamp(), TimeUnit.SECONDS);
            }

            batchPoints.point(point.build());

        }

        exchange.getIn().setBody(batchPoints.build());

    }

}
