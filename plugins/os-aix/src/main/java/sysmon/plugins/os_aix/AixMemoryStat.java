package sysmon.plugins.os_aix;

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

    private final Pattern pattern = Pattern.compile("^\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    private Long total;
    private Long used;
    private Long free;
    private Long pin;
    private Long virtual;
    private Long available;
    private Long paged;

    AixMemoryStat(List<String> lines) {
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() && matcher.groupCount() == 7) {
                total = Long.parseLong(matcher.group(1));
                used = Long.parseLong(matcher.group(2));
                free = Long.parseLong(matcher.group(3));
                pin = Long.parseLong(matcher.group(4));
                virtual = Long.parseLong(matcher.group(5));
                available = Long.parseLong(matcher.group(6));
                paged = Long.parseLong(matcher.group(7));
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
        fields.put("pin", pin);
        fields.put("virtual", virtual);
        fields.put("available", available);
        fields.put("paged", paged);
        fields.put("usage", usage.doubleValue());
        return fields;
    }

}
