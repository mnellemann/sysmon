package org.sysmon.shared;

import java.time.Instant;
import java.util.List;

public class MetricResult {
    
    private final String name;
    private final Instant timestamp;
    private List<MetricMeasurement> measurementList;

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now();
    }

    public void setMetricMeasurementList(List<MetricMeasurement> measurementList) {
        this.measurementList = measurementList;
    }

    public void addMetricMeasurement(MetricMeasurement measurement) {
        measurementList.add(measurement);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("%s - %s\n", timestamp.toString(), name));
        for(MetricMeasurement mm : measurementList) {
            sb.append(mm.toString()).append("\n");
        }

        return sb.toString();
    }
    
}
