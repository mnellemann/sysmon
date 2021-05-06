package org.sysmon.shared;

public class MetricMeasurement {

    private String name;
    private Object value;

    public MetricMeasurement() { }

    public MetricMeasurement(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return String.format("%s: %s", name, value);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
