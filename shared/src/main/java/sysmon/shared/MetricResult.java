package sysmon.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public class MetricResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String hostname;
    private Long timestamp;   // epoch milli
    private Measurement measurement;

    public MetricResult() {
    }

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public MetricResult(String name, Measurement measurement) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
        this.measurement = measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
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

    public Measurement getMeasurement() {
        return measurement;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s - %s {", timestamp.toString(), name));

        if(measurement != null &&  measurement.getTags() != null) {
            for (Map.Entry<String, String> entry : measurement.getTags().entrySet())
                sb.append(" [").append(entry.getKey()).append(": ").append(entry.getValue()).append("]");
        }

        if(measurement != null && measurement.getFields() != null) {
            for (Map.Entry<String,Object> entry : measurement.getFields().entrySet())
                sb.append(" [").append(entry.getKey()).append(": ").append(entry.getValue()).append("]");
        }

        return sb.append(" }").toString();
    }
    
}
