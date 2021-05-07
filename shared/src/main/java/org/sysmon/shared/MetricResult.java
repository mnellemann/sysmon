package org.sysmon.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MetricResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long timestamp;   // epoch milli
    private String hostname;
    private List<MeasurementPair> measurements = new ArrayList<>();

    public MetricResult() {
    }

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public void addMeasurements(List<MeasurementPair> measurementList) {
        this.measurements = measurementList;
    }

    public void addMeasurement(MeasurementPair measurement) {
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

    public List<MeasurementPair> getMeasurements() {
        return measurements;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s - %s\n", timestamp.toString(), name));
        for(MeasurementPair mm : measurements) {
            sb.append(mm.toString()).append("\n");
        }

        return sb.toString();
    }
    
}
