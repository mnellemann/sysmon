package sysmon.plugins.os_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LinuxDiskProcLine {

    // Sectors to bytes - each sector is 512 bytes - https://lkml.org/lkml/2015/8/17/269
    private static final int SECTOR_BYTE_SIZE = 512;

    private static final Logger log = LoggerFactory.getLogger(LinuxDiskProcLine.class);


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

    private Long readsCompleted = 0L;          // successfully
    //private Long readsMerged = 0L;
    private Long sectorsRead = 0L;             // 512 bytes pr. sector
    private Long timeSpentReading = 0L;        // ms
    private Long writesCompleted = 0L;         // successfully
    //private Long writesMerged = 0L;
    private Long sectorsWritten = 0L;          // 512 bytes pr. sector
    private Long timeSpentWriting = 0L;        // ms
    //private Long ioInProgress = 0L;
    private Long timeSpentOnIo = 0L;           // ms
    //private Long timeSpentOnIoWeighted = 0L;

    //private Long discardsCompleted = 0L;       // successfully
    //private Long discardsMerged = 0L;
    //private Long sectorsDiscarded = 0L;        // 512 bytes pr. sector
    //private Long timeSpentDiscarding = 0L;     // ms

    //private Long flushRequestsCompleted = 0L;
    //private Long timeSpentFlushing = 0L;       // ms


    LinuxDiskProcLine(List<String> procLines) {

        for(String procLine : procLines) {

            String[] splitStr = procLine.trim().split("\\s+");
            if (splitStr.length < 14) {
                throw new UnsupportedOperationException("Linux proc DISK string error: " + procLine);
            }

            //this.major = Integer.parseInt(splitStr[0]);
            //this.minor = Integer.parseInt(splitStr[1]);
            //this.device = splitStr[2];
            this.readsCompleted += Long.parseLong(splitStr[3]);
            //this.readsMerged += Long.parseLong(splitStr[4]);
            this.sectorsRead += Long.parseLong(splitStr[5]);
            this.timeSpentReading += Long.parseLong(splitStr[6]);
            this.writesCompleted += Long.parseLong(splitStr[7]);
            //this.writesMerged += Long.parseLong(splitStr[8]);
            this.sectorsWritten += Long.parseLong(splitStr[9]);
            this.timeSpentWriting += Long.parseLong(splitStr[10]);
            //this.ioInProgress += Long.parseLong(splitStr[11]);
            this.timeSpentOnIo += Long.parseLong(splitStr[12]);
            //this.timeSpentOnIoWeighted += Long.parseLong(splitStr[13]);

            /*
            if (splitStr.length >= 18) {
                this.discardsCompleted += Long.parseLong(splitStr[10]);
                this.discardsMerged += Long.parseLong(splitStr[11]);
                this.sectorsDiscarded += Long.parseLong(splitStr[12]);
                this.timeSpentDiscarding += Long.parseLong(splitStr[13]);
            } else {
                this.discardsCompleted = null;
                this.discardsMerged = null;
                this.sectorsDiscarded = null;
                this.timeSpentDiscarding = null;
            }

            if (splitStr.length == 20) {
                this.flushRequestsCompleted += Long.parseLong(splitStr[14]);
                this.timeSpentFlushing += Long.parseLong(splitStr[15]);
            } else {
                this.flushRequestsCompleted = null;
                this.timeSpentFlushing = null;
            }
             */

        }
    }

    public Long getTimeSpentOnIo() {
        return timeSpentOnIo;
    }

    public Long getBytesRead() {
        return sectorsRead * SECTOR_BYTE_SIZE;
    }

    public Long getBytesWritten() {
        return sectorsWritten * SECTOR_BYTE_SIZE;
    }

    public Long getTimeSpentReading() {
        return timeSpentReading;
    }

    public Long getTimeSpentWriting() {
        return timeSpentWriting;
    }

    public Long getTransactions() {
        return readsCompleted + writesCompleted;
    }

}
