package org.sysmon.plugins.sysmon_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxMemoryStat {

    private static final Logger log = LoggerFactory.getLogger(LinuxMemoryStat.class);

    /*
                  total        used        free      shared  buff/cache   available
    Mem:       16069172     5896832     4597860      639780     5574480     9192992
    Swap:       3985404           0     3985404
     */
    private final Pattern pattern = Pattern.compile("^Mem:\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    private Long total;
    private Long used;
    private Long free;
    private Long shared;
    private Long buffers;
    private Long available;
    private String mode;

    LinuxMemoryStat(List<String> lines) {
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() && matcher.groupCount() == 6) {
                total = Long.parseLong(matcher.group(1));
                used = Long.parseLong(matcher.group(2));
                free = Long.parseLong(matcher.group(3));
                shared = Long.parseLong(matcher.group(4));
                buffers = Long.parseLong(matcher.group(5));
                available = Long.parseLong(matcher.group(6));

                break;
            }
        }
    }


    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        return tags;
    }


    public Map<String, Object> getFields() {

        double tmp = ((double) (total - available)  / total ) * 100;
        BigDecimal usage = new BigDecimal(tmp).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> fields = new HashMap<>();
        fields.put("total", total);
        fields.put("used", used);
        fields.put("free", free);
        fields.put("shared", shared);
        fields.put("buffers", buffers);
        fields.put("available", available);
        fields.put("usage", usage.doubleValue());
        return fields;
    }

}
