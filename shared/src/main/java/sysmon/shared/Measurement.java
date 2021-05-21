package sysmon.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Measurement {

    private Map<String, String> tags = new HashMap<>();
    private Map<String, Object> fields = new HashMap<>();


    public Measurement() {
    }

    public Measurement(Map<String, String> tags, Map<String, Object> fields) {
        this.tags = Objects.requireNonNull(tags);
        this.fields = Objects.requireNonNull(fields);
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setTags(Map<String, String> tags) {
        Objects.requireNonNull(tags);
        this.tags = tags;
    }

    public void setFields(Map<String, Object> fields) {
        Objects.requireNonNull(fields);
        this.fields = fields;
    }

}
