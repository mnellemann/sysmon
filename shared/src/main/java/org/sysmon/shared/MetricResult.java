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
    private List<MetricMeasurement> measurementList = new ArrayList<>();

    public MetricResult() {

    }

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public void setMetricMeasurementList(List<MetricMeasurement> measurementList) {
        this.measurementList = measurementList;
    }

    public void addMetricMeasurement(MetricMeasurement measurement) {
        measurementList.add(measurement);
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

    public List<MetricMeasurement> getMeasurementList() {
        return measurementList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s - %s\n", timestamp.toString(), name));
        for(MetricMeasurement mm : measurementList) {
            sb.append(mm.toString()).append("\n");
        }

        return sb.toString();
    }
    
}
