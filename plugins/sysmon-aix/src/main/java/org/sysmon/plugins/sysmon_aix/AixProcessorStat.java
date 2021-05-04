package org.sysmon.plugins.sysmon_aix;

public class AixProcessorStat {

    private final Integer cpuNum;
    private final Float userTime;
    private final Float systemTime;
    private final Float waitTime;
    private final Float idleTime;

    AixProcessorStat(String procString) {

        // cpu    min    maj   mpcs   mpcr    dev   soft    dec     ph     cs    ics  bound     rq   push S3pull  S3grd  S0rd  S1rd  S2rd  S3rd  S4rd  S5rd   sysc    us    sy    wa    id    pc   %ec   ilcs   vlcs S3hrd S4hrd S5hrd  %nsp
        String[] splitStr = procString.trim().split("\\s+");
        if(splitStr.length != 35) {
            throw new UnsupportedOperationException("AIX mpstat CPU string error: " + procString);
        }

        this.cpuNum = Integer.parseInt(splitStr[0]);
        this.userTime = Float.parseFloat(splitStr[23]);
        this.systemTime = Float.parseFloat(splitStr[24]);
        this.waitTime = Float.parseFloat(splitStr[25]);
        this.idleTime = Float.parseFloat(splitStr[26]);


    }

    public Integer getCpuNum() {
        return cpuNum;
    }

    public Float getUserTime() {
        return userTime;
    }

    public Float getSystemTime() {
        return systemTime;
    }

    public Float getIdleTime() {
        return idleTime;
    }

    public Float getWaitTime() {
        return waitTime;
    }


    public Float getCombinedWorkTime() {
        return userTime + systemTime;
    }

    public Float getCombinedTime() {
        return getIdleTime() + getCombinedWorkTime();
    }

    public float getUtilizationPercentage() {
        return 100 - idleTime;
    }

}
