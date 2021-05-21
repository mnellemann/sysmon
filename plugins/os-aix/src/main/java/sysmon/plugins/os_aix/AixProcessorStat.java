package sysmon.plugins.os_aix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixProcessorStat {

    // System configuration: type=Shared mode=Uncapped smt=8 lcpu=8 mem=4096MB psize=19 ent=0.50
    private final Pattern patternAix = Pattern.compile("^System configuration: type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+)MB psize=(\\d+) ent=(\\d+\\.?\\d*)");

    // type=Shared mode=Uncapped smt=8 lcpu=4 mem=4101120 kB cpus=24 ent=4.00
    private final Pattern patternLinux = Pattern.compile("^type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+) kB cpus=(\\d+) ent=(\\d+\\.?\\d*)");


    private String type;
    private String mode;
    private int smt;
    private int lcpu;
    private int psize;
    private float ent;

    private final float user;   // Indicates the percentage of the entitled processing capacity used while executing at the user level (application).
    private final float sys;    // Indicates the percentage of the entitled processing capacity used while executing at the system level (kernel).
    private final float wait;   // Indicates the percentage of the entitled processing capacity unused while the partition was idle and had outstanding disk I/O request(s).
    private final float idle;   // Indicates the percentage of the entitled processing capacity unused while the partition was idle and did not have any outstanding disk I/O request.
    private final float physc;  // Indicates the number of physical processors consumed.
    private final float entc;   // Indicates the percentage of the entitled capacity consumed.
    private final float lbusy;  // Indicates the percentage of logical processor(s) utilization that occurred while executing at the user and system level.


    AixProcessorStat(List<String> lines) {

        for (String line : lines) {

            if (line.startsWith("System configuration:")) {
                Matcher matcher = patternAix.matcher(line);
                if (matcher.find() && matcher.groupCount() == 7) {
                    type = matcher.group(1);
                    mode = matcher.group(2);
                    smt = Integer.parseInt(matcher.group(3));
                    lcpu = Integer.parseInt(matcher.group(4));
                    psize = Integer.parseInt(matcher.group(5));
                    ent = Float.parseFloat(matcher.group(7));
                }
            }

            if (line.startsWith("type=")) {
                //type=Shared mode=Uncapped smt=8 lcpu=4 mem=4101120 kB cpus=24 ent=4.00
                Matcher matcher = patternLinux.matcher(line);
                if (matcher.find() && matcher.groupCount() == 7) {
                    type = matcher.group(1);
                    mode = matcher.group(2);
                    smt = Integer.parseInt(matcher.group(3));
                    lcpu = Integer.parseInt(matcher.group(4));
                    psize = Integer.parseInt(matcher.group(6));
                    ent = Float.parseFloat(matcher.group(7));
                }
            }

        }

        String lparstat = lines.get(lines.size() -1);
        String[] splitStr = lparstat.trim().split("\\s+");
        if(splitStr.length < 9) {
            throw new UnsupportedOperationException("lparstat string error: " + lparstat);
        }

        this.user = Float.parseFloat(splitStr[0]);
        this.sys = Float.parseFloat(splitStr[1]);
        this.wait = Float.parseFloat(splitStr[2]);
        this.idle = Float.parseFloat(splitStr[3]);
        this.physc = Float.parseFloat(splitStr[4]);
        this.entc = Float.parseFloat(splitStr[5]);
        this.lbusy = Float.parseFloat(splitStr[6]);

    }

    public float getUser() {
        return user;
    }

    public float getSys() {
        return sys;
    }

    public float getIdle() {
        return idle;
    }

    public float getWait() {
        return wait;
    }

    public float getPhysc() {
        return physc;
    }

    public float getEntc() {
        return entc;
    }

    public float getLbusy() {
        return lbusy;
    }

    public float getUsage() {
        return 100 - idle;
    }

    public Map<String, String> getTags() {
        return new HashMap<>();
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("lcpu", lcpu);
        fields.put("ent", ent);
        fields.put("user", user);
        fields.put("sys", sys);
        fields.put("idle", idle);
        fields.put("wait", wait);
        fields.put("physc", physc);
        fields.put("entc", entc);
        fields.put("lbusy", lbusy);
        fields.put("mode", mode);
        fields.put("type", type);
        return fields;
    }
}
