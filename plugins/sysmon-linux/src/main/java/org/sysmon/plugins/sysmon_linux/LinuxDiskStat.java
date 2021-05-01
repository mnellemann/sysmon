package org.sysmon.plugins.sysmon_linux;

public class LinuxDiskStat {
    /*
        ==  ===================================
         1  major number
		 2  minor mumber
		 3  device name
		 4  reads completed successfully
		 5  reads merged
		 6  sectors read
		 7  time spent reading (ms)
		 8  writes completed
		 9  writes merged
		10  sectors written
		11  time spent writing (ms)
		12  I/Os currently in progress
		13  time spent doing I/Os (ms)
		14  weighted time spent doing I/Os (ms)
		==  ===================================

    Kernel 4.18+ appends four more fields for discard
    tracking putting the total at 18:

        ==  ===================================
        15  discards completed successfully
		16  discards merged
		17  sectors discarded
		18  time spent discarding
		==  ===================================

    Kernel 5.5+ appends two more fields for flush requests:

        ==  =====================================
        19  flush requests completed successfully
		20  time spent flushing
		==  =====================================
    */

    private final int major;
    private final int minor;
    private final String device;                // device name
    private final Long readsCompleted;          // successfully
    private final Long readsMerged;
    private final Long sectorsRead;             // 512 bytes pr. sector
    private final Long timeSpentReading;        // ms
    private final Long writesCompleted;         // successfully
    private final Long writesMerged;
    private final Long sectorsWritten;          // 512 bytes pr. sector
    private final Long timeSpentWriting;        // ms
    private final Long ioInProgress;
    private final Long timeSpentOnIo;           // ms
    private final Long timeSpentOnIoWeighted;

    private final Long discardsCompleted;       // successfully
    private final Long discardsMerged;
    private final Long sectorsDiscarded;        // 512 bytes pr. sector
    private final Long timeSpentDiscarding;     // ms

    private final Long flushRequestsCompleted;
    private final Long timeSpentFlushing;       // ms


    LinuxDiskStat(String procString) {

        String[] splitStr = procString.trim().split("\\s+");
        if(splitStr.length < 14) {
            throw new UnsupportedOperationException("Linux proc DISK string error: " + procString);
        }

        this.major = Integer.parseInt(splitStr[0]);
        this.minor = Integer.parseInt(splitStr[1]);
        this.device = splitStr[2];
        this.readsCompleted = Long.parseLong(splitStr[3]);
        this.readsMerged = Long.parseLong(splitStr[4]);
        this.sectorsRead = Long.parseLong(splitStr[5]);
        this.timeSpentReading = Long.parseLong(splitStr[6]);
        this.writesCompleted = Long.parseLong(splitStr[7]);
        this.writesMerged = Long.parseLong(splitStr[8]);
        this.sectorsWritten = Long.parseLong(splitStr[9]);
        this.timeSpentWriting = Long.parseLong(splitStr[10]);
        this.ioInProgress = Long.parseLong(splitStr[11]);
        this.timeSpentOnIo = Long.parseLong(splitStr[12]);
        this.timeSpentOnIoWeighted = Long.parseLong(splitStr[13]);

        if(splitStr.length >= 18) {
            this.discardsCompleted = Long.parseLong(splitStr[10]);
            this.discardsMerged = Long.parseLong(splitStr[11]);
            this.sectorsDiscarded = Long.parseLong(splitStr[12]);
            this.timeSpentDiscarding = Long.parseLong(splitStr[13]);
        } else {
            this.discardsCompleted = null;
            this.discardsMerged = null;
            this.sectorsDiscarded = null;
            this.timeSpentDiscarding = null;
        }

        if(splitStr.length == 20) {
            this.flushRequestsCompleted = Long.parseLong(splitStr[14]);
            this.timeSpentFlushing = Long.parseLong(splitStr[15]);
        } else {
            this.flushRequestsCompleted = null;
            this.timeSpentFlushing = null;
        }

    }

    public String getDevice() {
        return device;
    }

    public Long getTimeSpentOnIo() {
        return timeSpentOnIo;
    }


}