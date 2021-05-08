package org.sysmon.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long timestamp;   // epoch milli
    private String hostname;
    private List<Measurement> measurements = new ArrayList<>();

    public MetricResult() {
    }

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public void addMeasurements(List<Measurement> measurementList) {
        this.measurements = measurementList;
    }

    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getHostname() {
        return hostname;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s - %s\n", timestamp.toString(), name));
        for(Measurement m : measurements) {

            for (Map.Entry<String,String> entry : m.getTags().entrySet())
                sb.append(entry.getKey() + " : " + entry.getValue());

            for (Map.Entry<String,Object> entry : m.getFields().entrySet())
                sb.append(entry.getKey() + " : " + entry.getValue());

            sb.append(m.toString()).append("\n");
        }

        return sb.toString();
    }
    
}
