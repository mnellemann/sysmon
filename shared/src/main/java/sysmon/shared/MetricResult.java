package sysmon.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;

public class MetricResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String hostname;
    private Long timestamp;   // epoch seconds
    private ArrayList<Measurement> measurements;

    public MetricResult() {
    }

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public MetricResult(String name, Measurement measurement) {
        this.name = name;
        this.timestamp = Instant.now().getEpochSecond();
        this.measurements = new ArrayList<Measurement>() {{
                add(measurement);
        }};
    }

    public MetricResult(String name, ArrayList<Measurement> measurements) {
        this.name = name;
        this.timestamp = Instant.now().getEpochSecond();
        this.measurements = measurements;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurements = new ArrayList<Measurement>() {{
                add(measurement);
        }};
    }

    public void setMeasurements(ArrayList<Measurement> measurements) {
        this.measurements = measurements;
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

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("From %s: ", name));

        if(measurements != null && !measurements.isEmpty()) {
            sb.append(String.format("%d measurement(s) ", measurements.size()));
            for(Measurement m : measurements) {
                sb.append(String.format("{ tags: %d, fields: %d } ", m.getTags().size(), m.getFields().size()));
            }
        }

        return sb.toString();
    }

}
