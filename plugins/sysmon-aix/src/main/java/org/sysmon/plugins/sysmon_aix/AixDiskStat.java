package org.sysmon.plugins.sysmon_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixDiskStat {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorStat.class);

    // Disks:         % tm_act     Kbps      tps    Kb_read   Kb_wrtn
    // hdisk0            1.0     752.0      81.0        740        12
    private final Pattern pattern = Pattern.compile("^(hdisk\\d+)\\s+(\\d+\\.?\\d*)\\s+\\s+(\\d+\\.?\\d*)\\s+\\s+(\\d+\\.?\\d*)\\s+(\\d+)\\s+(\\d+)");


    private String device;
    private Float tmAct;    // Indicates the percentage of time the physical disk/tape was active (bandwidth utilization for the drive).
    private Float kbps;     // Indicates the amount of data transferred (read or written) to the drive in KB per second.
    private Float tps;      // Indicates the number of transfers per second that were issued to the physical disk/tape. A transfer is an I/O request to the physical disk/tape. Multiple logical requests can be combined into a single I/O request to the disk. A transfer is of indeterminate size.
    private Long kbRead;    // The total number of KB read.
    private Long kbWritten; // The total number of KB written.


    AixDiskStat(List<String> lines) {

        for (String line : lines) {

            if (line.startsWith("hdisk")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find() && matcher.groupCount() == 6) {
                    device = matcher.group(1);
                    tmAct = Float.parseFloat(matcher.group(2));
                    kbps = Float.parseFloat(matcher.group(3));
                    tps = Float.parseFloat(matcher.group(4));
                    kbRead = Long.parseLong(matcher.group(5));
                    kbWritten = Long.parseLong(matcher.group(6));
                    break;
                }

            }

        }

    }

    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("device", device);
        return tags;
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("reads", kbRead * 1024);     // from Kb to b
        fields.put("writes", kbWritten * 1024); // from Kb to b


        return fields;
    }
}
