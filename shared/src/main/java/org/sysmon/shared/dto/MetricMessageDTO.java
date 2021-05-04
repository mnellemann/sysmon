package org.sysmon.shared.dto;

import java.util.Objects;

public class MetricMessageDTO {

    private String msg;
    private long id;

    public MetricMessageDTO() {
        // empty constructor is required bu Jackson for deserialization
    }

    public MetricMessageDTO(String msg, long id) {
        Objects.requireNonNull(msg);

        this.msg = msg;
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public long getId() {
        return id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MetricMessageDTO{" +
                "msg='" + msg + '\'' +
                ", id=" + id +
                '}';
    }

}
