package sysmon.plugins.os_linux;

public class LinuxProcessorProcLine {

    private final String cpuName;
    private final Long userTime;
    private final Long niceTime;
    private final Long systemTime;
    private final Long idleTime;
    private final Long ioWaitTime;
    private final Long irqTime;
    private final Long softIrqTime;
    private final Long stealTime;
    private final Long guestTime;
    private final Long guestNiceTime;


    public LinuxProcessorProcLine(String procString) {

        String[] splitStr = procString.trim().split("\\s+");
        if(splitStr.length != 11) {
            throw new UnsupportedOperationException("Linux proc CPU string error: " + procString);
        }

        this.cpuName = splitStr[0];
        this.userTime = Long.parseLong(splitStr[1]);
        this.niceTime = Long.parseLong(splitStr[2]);
        this.systemTime = Long.parseLong(splitStr[3]);
        this.idleTime = Long.parseLong(splitStr[4]);
        this.ioWaitTime = Long.parseLong(splitStr[5]);
        this.irqTime = Long.parseLong(splitStr[6]);
        this.softIrqTime = Long.parseLong(splitStr[7]);
        this.stealTime = Long.parseLong(splitStr[8]);
        this.guestTime = Long.parseLong(splitStr[9]);
        this.guestNiceTime = Long.parseLong(splitStr[10]);

    }

    public String getCpuName() {
        return cpuName;
    }

    public Long getUserTime() {
        return userTime;
    }

    public Long getNiceTime() {
        return niceTime;
    }

    public Long getSystemTime() {
        return systemTime;
    }

    public Long getIdleTime() {
        return idleTime;
    }

    public Long getIoWaitTime() {
        return ioWaitTime;
    }

    public Long getIrqTime() {
        return irqTime;
    }

    public Long getSoftIrqTime() {
        return softIrqTime;
    }

    public Long getStealTime() {
        return stealTime;
    }

    public Long getGuestTime() {
        return guestTime;
    }

    public Long getGuestNiceTime() {
        return guestNiceTime;
    }

    public Long getCombinedIdleTime() {
        return idleTime + ioWaitTime;
    }

    public Long getCombinedWorkTime() {
        return userTime + niceTime + systemTime + irqTime + softIrqTime + stealTime + guestTime + guestNiceTime;
    }

    public Long getCombinedTime() {
        return getCombinedIdleTime() + getCombinedWorkTime();
    }

}
