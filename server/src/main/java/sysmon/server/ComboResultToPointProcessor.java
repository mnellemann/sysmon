package sysmon.server;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import sysmon.shared.ComboResult;
import sysmon.shared.Measurement;
import sysmon.shared.MetricResult;

public class ComboResultToPointProcessor implements Processor {

    private static String influxDbName;
    private boolean localTime = false;


    ComboResultToPointProcessor(String influxDbName) {
        ComboResultToPointProcessor.influxDbName = influxDbName;
    }


    ComboResultToPointProcessor(String influxDbName, boolean localTime) {
        ComboResultToPointProcessor.influxDbName = influxDbName;
        this.localTime = localTime;
    }


    @Override
    public void process(Exchange exchange) {

        ComboResult comboResult = exchange.getIn().getBody(ComboResult.class);

        BatchPoints.Builder batchPoints = BatchPoints
                .database(ComboResultToPointProcessor.influxDbName)
                .precision(TimeUnit.SECONDS);

        for(MetricResult metricResult : comboResult.getMetricResults()) {

            for(Measurement measurement : metricResult.getMeasurements()) {

                Point.Builder point = Point.measurement(metricResult.getName())
                        .tag("hostname", metricResult.getHostname())
                        .tag(measurement.getTags())
                        .fields(measurement.getFields());

                // Override timestamp client
                if(localTime) {
                    point.time(Instant.now().getEpochSecond(), TimeUnit.SECONDS);
                } else {
                    point.time(metricResult.getTimestamp(), TimeUnit.SECONDS);
                }

                batchPoints.point(point.build());
            }

        }

        exchange.getIn().setBody(batchPoints.build());

    }

}
