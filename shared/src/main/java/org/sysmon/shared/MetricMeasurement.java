package org.sysmon.shared;

public class MetricMeasurement {

    private String name;
    private Object value;

    public MetricMeasurement(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return String.format("%s: %s", name, value);
    }

}
