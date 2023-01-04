package sysmon.shared;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Measurement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> tags = new TreeMap<>();
    private Map<String, Object> fields = new TreeMap<>();


    public Measurement() {
    }

    public Measurement(Map<String, String> tags, TreeMap<String, Object> fields) {
        this.tags = Objects.requireNonNull(tags);
        this.fields = Objects.requireNonNull(fields);
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setTags(TreeMap<String, String> tags) {
        Objects.requireNonNull(tags);
        this.tags = tags;
    }

    public void setFields(TreeMap<String, Object> fields) {
        Objects.requireNonNull(fields);
        this.fields = fields;
    }

}
