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
                .precision(TimeUnit.MILLISECONDS);

        for(MetricResult metricResult : comboResult.getMetricResults()) {

            for(Measurement measurement : metricResult.getMeasurements()) {

                Point.Builder point = Point.measurement(metricResult.getName())
                        .time(metricResult.getTimestamp(), TimeUnit.MILLISECONDS)
                        .tag("hostname", metricResult.getHostname());

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

        }

        exchange.getIn().setBody(batchPoints.build());

    }

}
