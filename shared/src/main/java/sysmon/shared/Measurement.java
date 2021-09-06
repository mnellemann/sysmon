package sysmon.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Measurement implements Serializable {

    private HashMap<String, String> tags = new HashMap<>();
    private HashMap<String, Object> fields = new HashMap<>();


    public Measurement() {
    }

    public Measurement(HashMap<String, String> tags, HashMap<String, Object> fields) {
        this.tags = Objects.requireNonNull(tags);
        this.fields = Objects.requireNonNull(fields);
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setTags(HashMap<String, String> tags) {
        Objects.requireNonNull(tags);
        this.tags = tags;
    }

    public void setFields(HashMap<String, Object> fields) {
        Objects.requireNonNull(fields);
        this.fields = fields;
    }

}
