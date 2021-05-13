package org.sysmon.plugins.sysmon_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixMemoryStat {

    private static final Logger log = LoggerFactory.getLogger(AixMemoryStat.class);

    //                size       inuse        free         pin     virtual  available   mmode
    // memory      4194304     4036532      157772     1854772     2335076    1652640     Ded
    private final Pattern patternAix = Pattern.compile("^memory\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)");

    private Long total;
    private Long used;
    private Long free;
    private Long virtual;
    private Long available;
    private String mode;

    AixMemoryStat(List<String> lines) {
        for (String line : lines) {
            Matcher matcher = patternAix.matcher(line);
            if (matcher.find() && matcher.groupCount() == 7) {
                total = Long.parseLong(matcher.group(1));
                used = Long.parseLong(matcher.group(2));
                free = Long.parseLong(matcher.group(3));
                //pin = Long.parseLong(matcher.group(4));
                virtual = Long.parseLong(matcher.group(5));
                available = Long.parseLong(matcher.group(6));
                mode = matcher.group(7);
                break;
            }
        }
    }


    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("mode", mode);
        return tags;
    }


    public Map<String, Object> getFields() {

        double tmp = ((double) (total - available)  / total ) * 100;
        BigDecimal usage = new BigDecimal(tmp).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> fields = new HashMap<>();
        fields.put("total", total);
        fields.put("used", used);
        fields.put("free", free);
        fields.put("virtual", virtual);
        fields.put("available", available);
        fields.put("usage", usage.doubleValue());
        return fields;
    }

}
