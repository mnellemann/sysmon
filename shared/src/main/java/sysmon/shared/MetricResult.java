package sysmon.shared;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class MetricResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String hostname;
    private Long timestamp;   // epoch milli
    private ArrayList<Measurement> measurements;

    public MetricResult() {
    }

    public MetricResult(String name) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public MetricResult(String name, Measurement measurement) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
        this.measurements = new ArrayList<Measurement>() {{
                add(measurement);
        }};
    }

    public MetricResult(String name, ArrayList<Measurement> measurements) {
        this.name = name;
        this.timestamp = Instant.now().toEpochMilli();
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
        StringBuilder sb = new StringBuilder(String.format("%s - %s => ", timestamp.toString(), name));

        if(measurements != null && !measurements.isEmpty()) {

            for(Measurement m : measurements) {

                sb.append("{ ");
                if(m != null &&  m.getTags() != null) {
                    for (Map.Entry<String, String> entry : m.getTags().entrySet())
                        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
                }
                sb.append("} ");

                /*
                sb.append("[ ");
                if(m != null && m.getFields() != null) {
                    for (Map.Entry<String,Object> entry : m.getFields().entrySet())
                        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
                }
                sb.append("] ");
                 */

            }
        }

        return sb.toString();
    }
    
}
