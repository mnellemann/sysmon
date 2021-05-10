package org.sysmon.plugins.sysmon_linux;

import java.util.HashMap;
import java.util.Map;

public class LinuxProcessorStat {

    private final String cpuName;
    //private final float user;
    //private final float sys;
    //private final float wait;
    //private final float idle;
    private final float busy;

    public LinuxProcessorStat(LinuxProcessorProcLine current, LinuxProcessorProcLine previous) {
        cpuName = current.getCpuName();

        long workTimeDiff = current.getCombinedTime() - previous.getCombinedTime();
        long idleTimeDiff = current.getCombinedIdleTime() - previous.getCombinedIdleTime();

        float utilization = (float) (workTimeDiff - idleTimeDiff) / workTimeDiff;
        busy = (utilization * 100);

        // TODO: Calculate user, system, idle and wait diff times into percentage.
    }


    public String getName() {
        return cpuName;
    }


    public Float getBusy() {
        return busy;
    }

    public Map<String, String> getTags() {
        return new HashMap<>();
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("busy", busy);
        return fields;
    }


}
