package sysmon.plugins.os_linux;

import java.util.HashMap;
import java.util.Map;

public class LinuxProcessorStat {

    private final float user;
    private final float sys;
    private final float wait;
    private final float idle;
    private final float busy;

    public LinuxProcessorStat(LinuxProcessorProcLine previous, LinuxProcessorProcLine current) {

        long workTime = current.getCombinedTime() - previous.getCombinedTime();

        long busyTime = current.getCombinedIdleTime() - previous.getCombinedIdleTime();
        float busyDiff = (float) (workTime - busyTime) / workTime;
        busy = (busyDiff * 100);

        long userTime = current.getUserTime() - previous.getUserTime();
        float userDiff = (float) (workTime - userTime) / workTime;
        user = 100 - (userDiff * 100);

        long sysTime = current.getSystemTime() - previous.getSystemTime();
        float sysDiff = (float) (workTime - sysTime) / workTime;
        sys = 100 - (sysDiff * 100);

        long waitTime = current.getIoWaitTime() - previous.getIoWaitTime();
        float waitDiff = (float) (workTime - waitTime) / workTime;
        wait = 100 - (waitDiff * 100);

        long idleTime = current.getIdleTime() - previous.getIdleTime();
        float idleDiff = (float) (workTime - idleTime) / workTime;
        idle = 100 - (idleDiff * 100);

    }


    public Float getBusy() {
        return busy;
    }

    public Map<String, String> getTags() {
        return new HashMap<>();
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("user", user);
        fields.put("sys", sys);
        fields.put("wait", wait);
        fields.put("idle", idle);
        fields.put("busy", busy);
        return fields;
    }


}
