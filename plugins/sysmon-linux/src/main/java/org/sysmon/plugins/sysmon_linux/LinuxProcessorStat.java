package org.sysmon.plugins.sysmon_linux;

import org.sysmon.shared.MeasurementPair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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


    public MeasurementPair getMeasurements() {
        return new MeasurementPair(cpuName, utilizationPercentage);
    }


}
