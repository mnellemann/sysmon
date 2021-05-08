package org.sysmon.plugins.sysmon_linux;

import java.util.HashMap;
import java.util.Map;

public class LinuxProcessorStat {

    private final String cpuName;
    private final float utilizationPercentage;

    public LinuxProcessorStat(LinuxProcessorProcLine current, LinuxProcessorProcLine previous) {
        cpuName = current.getCpuName();

        long workTimeDiff = current.getCombinedTime() - previous.getCombinedTime();
        long idleTimeDiff = current.getCombinedIdleTime() - previous.getCombinedIdleTime();

        float utilization = (float) (workTimeDiff - idleTimeDiff) / workTimeDiff;
        utilizationPercentage = (utilization * 100);
    }


    public String getName() {
        return cpuName;
    }


    public Map<String, Object> getFields() {

        HashMap<String, Object> fieldsMap = new HashMap<>();
        fieldsMap.put("utilization", utilizationPercentage);

        return fieldsMap;

    }


}
