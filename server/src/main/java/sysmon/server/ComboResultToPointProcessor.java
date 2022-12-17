package sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import sysmon.shared.ComboResult;
import sysmon.shared.Measurement;
import sysmon.shared.MetricResult;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ComboResultToPointProcessor implements Processor {

    private static String influxDbName;

    ComboResultToPointProcessor(String influxDbName) {
        ComboResultToPointProcessor.influxDbName = influxDbName;
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
                        .time(metricResult.getTimestamp(), TimeUnit.SECONDS)
                        .tag("hostname", metricResult.getHostname())
                        .tag(measurement.getTags())
                        .fields(measurement.getFields());

                batchPoints.point(point.build());
            }

        }

        exchange.getIn().setBody(batchPoints.build());

    }

}
