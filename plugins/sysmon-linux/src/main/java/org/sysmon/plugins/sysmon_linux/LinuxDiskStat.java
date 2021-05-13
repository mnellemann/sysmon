package org.sysmon.plugins.sysmon_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LinuxDiskStat {

    private static final Logger log = LoggerFactory.getLogger(LinuxDiskStat.class);

    private String device;
    private Long iotime;
    private Long reads;
    private Long writes;
    private Long readTime;
    private Long writeTime;

    LinuxDiskStat(LinuxDiskProcLine proc1, LinuxDiskProcLine proc2) {

        device = proc1.getDevice();
        iotime = proc2.getTimeSpentOnIo() - proc1.getTimeSpentOnIo();
        writes = proc2.getSectorsWritten() - proc1.getSectorsWritten();
        reads = proc2.getSectorsRead() - proc1.getSectorsRead();

    }

    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("device", device);
        return tags;
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("iotime", iotime);
        fields.put("writes", writes);
        fields.put("reads", reads);
        return fields;
    }
}