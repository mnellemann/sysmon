package sysmon.plugins.os_aix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixDiskStat {

    // Disks:         % tm_act     Kbps      tps    Kb_read   Kb_wrtn
    // hdisk0            1.0     752.0      81.0        740        12
    private final Pattern pattern = Pattern.compile("^(hdisk\\d+)\\s+(\\d+\\.?\\d*)\\s+\\s+(\\d+\\.?\\d*)\\s+\\s+(\\d+\\.?\\d*)\\s+(\\d+)\\s+(\\d+)");

    //private String device;
    //private Float tmAct = 0.0f;     // Indicates the percentage of time the physical disk/tape was active (bandwidth utilization for the drive).
    private float kbps = 0.0f;      // Indicates the amount of data transferred (read or written) to the drive in KB per second.
    private float tps = 0.0f;       // Indicates the number of transfers per second that were issued to the physical disk/tape. A transfer is an I/O request to the physical disk/tape. Multiple logical requests can be combined into a single I/O request to the disk. A transfer is of indeterminate size.
    private long kbRead = 0L;       // The total number of KB read.
    private long kbWritten = 0L;    // The total number of KB written.


    AixDiskStat(List<String> lines) {

        for (String line : lines) {
            if (line.startsWith("hdisk")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find() && matcher.groupCount() == 6) {
                    //device = matcher.group(1);
                    //tmAct = Float.parseFloat(matcher.group(2));
                    kbps += Float.parseFloat(matcher.group(3));
                    tps += Float.parseFloat(matcher.group(4));
                    kbRead += Long.parseLong(matcher.group(5));
                    kbWritten += Long.parseLong(matcher.group(6));
                }
            }
        }

    }

    public Map<String, String> getTags() {
        return new HashMap<>();
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("reads", kbRead * 1024);     // from Kb to bytes
        fields.put("writes", kbWritten * 1024); // from Kb to bytes
        fields.put("kbps", (int) kbps);
        fields.put("tps", (int) tps);
        return fields;
    }
}
