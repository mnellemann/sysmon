package sysmon.plugins.os_linux;

public class LinuxProcessorProcLine {

    private final String cpuName;
    private final long userTime;
    private final long niceTime;
    private final long systemTime;
    private final long idleTime;
    private final long ioWaitTime;
    private final long irqTime;
    private final long softIrqTime;
    private final long stealTime;
    private final long guestTime;
    private final long guestNiceTime;


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

    public long getUserTime() {
        return userTime;
    }

    public long getNiceTime() {
        return niceTime;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public long getIoWaitTime() {
        return ioWaitTime;
    }

    public long getIrqTime() {
        return irqTime;
    }

    public long getSoftIrqTime() {
        return softIrqTime;
    }

    public long getStealTime() {
        return stealTime;
    }

    public long getGuestTime() {
        return guestTime;
    }

    public long getGuestNiceTime() {
        return guestNiceTime;
    }

    public long getCombinedIdleTime() {
        return idleTime + ioWaitTime;
    }

    public long getCombinedWorkTime() {
        return userTime + niceTime + systemTime + irqTime + softIrqTime + stealTime + guestTime + guestNiceTime;
    }

    public long getCombinedTime() {
        return getCombinedIdleTime() + getCombinedWorkTime();
    }

}
