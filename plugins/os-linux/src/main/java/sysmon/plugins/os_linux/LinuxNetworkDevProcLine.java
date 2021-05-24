package sysmon.plugins.os_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxNetworkDevProcLine {

    private static final Logger log = LoggerFactory.getLogger(LinuxNetworkDevProcLine.class);

    private static final Pattern pattern1 = Pattern.compile("^\\s+([a-z]{2,}[0-9]+):.*");
    private static final Pattern pattern2 = Pattern.compile("^\\s+([a-z]{2,}[0-9]+):\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    private long rxBytes;
    private long rxPackets;
    private long rxErrs;

    private long txBytes;
    private long txPackets;
    private long txErrs;

    /*
    Inter-|   Receive                                                |  Transmit
 face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets errs drop fifo colls carrier compressed
  env2: 657010764  483686    0    0    0     0          0         0 55416850  431020    0    0    0     0       0          0
  env3: 6900272   41836    0    0    0     0          0         0  7667444   41849    0    0    0     0       0          0
    lo: 3098805   14393    0    0    0     0          0         0  3098805   14393    0    0    0     0       0          0

     */

    public LinuxNetworkDevProcLine(List<String> procLines) {

        Matcher matcher1;
        Matcher matcher2;
        for(String procLine : procLines) {

            matcher1 = pattern1.matcher(procLine);
            if(matcher1.matches()) {

                if(matcher1.group(1).equals("lo")) {
                    continue;
                }

                matcher2 = pattern2.matcher(procLine);
                if(matcher2.matches() && matcher2.groupCount() == 17) {

                    rxBytes += Long.parseLong(matcher2.group(2));
                    rxPackets += Long.parseLong(matcher2.group(3));
                    rxErrs += Long.parseLong(matcher2.group(4));

                    txBytes += Long.parseLong(matcher2.group(10));
                    txPackets += Long.parseLong(matcher2.group(11));
                    txErrs += Long.parseLong(matcher2.group(12));
                }

            }


        }
    }


    public long getRxBytes() {
        return rxBytes;
    }

    public long getRxPackets() {
        return rxPackets;
    }

    public long getRxErrs() {
        return rxErrs;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public long getTxPackets() {
        return txPackets;
    }

    public long getTxErrs() {
        return txErrs;
    }

}
