package sysmon.plugins.os_aix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixMemoryStat {

    private final Pattern pattern = Pattern.compile("^\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    private long total;
    private long used;
    private long free;
    private long pin;
    private long virtual;
    private long available;
    private long paged;

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
        return new HashMap<>();
    }


    public Map<String, Object> getFields() {

        float usage = ((float) (total - available)  / total ) * 100;
        //BigDecimal usage = new BigDecimal(tmp).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> fields = new HashMap<>();
        fields.put("total", total);
        fields.put("used", used);
        fields.put("free", free);
        fields.put("pin", pin);
        fields.put("virtual", virtual);
        fields.put("available", available);
        fields.put("paged", paged);
        fields.put("usage", usage);
        return fields;
    }

}
