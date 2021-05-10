package org.sysmon.plugins.sysmon_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixProcessorStat {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorStat.class);

    private final Pattern pattern = Pattern.compile("^System configuration: type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+)MB psize=(\\d+) ent=(\\d+\\.?\\d*)");

    private String type;
    private String mode;
    private Integer smt;
    private Integer lcpu;
    private Integer psize;
    private Float ent;

    private final Float user;   // Indicates the percentage of the entitled processing capacity used while executing at the user level (application).
    private final Float sys;    // Indicates the percentage of the entitled processing capacity used while executing at the system level (kernel).
    private final Float wait;   // Indicates the percentage of the entitled processing capacity unused while the partition was idle and had outstanding disk I/O request(s).
    private final Float idle;   // Indicates the percentage of the entitled processing capacity unused while the partition was idle and did not have any outstanding disk I/O request.
    private final Float physc;  // Indicates the number of physical processors consumed.
    private final Float entc;   // Indicates the percentage of the entitled capacity consumed.
    private final Float lbusy;  // Indicates the percentage of logical processor(s) utilization that occurred while executing at the user and system level.


    /*

    System configuration: type=Shared mode=Uncapped smt=8 lcpu=8 mem=4096MB psize=19 ent=0.50


    %user  %sys  %wait  %idle physc %entc  lbusy  vcsw phint  %nsp  %utcyc
    ----- ----- ------ ------ ----- ----- ------ ----- ----- -----  ------
      0.1   0.0    0.0   99.9  0.00   0.2    1.9 37441986   316   149  33.06
    */
    AixProcessorStat(List<String> vmstatLines) {

        for(String line : vmstatLines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() && matcher.groupCount() == 7) {
                type=matcher.group(1);
                mode=matcher.group(2);
                smt = Integer.parseInt(matcher.group(3));
                lcpu = Integer.parseInt(matcher.group(4));
                psize = Integer.parseInt(matcher.group(5));
                ent = Float.parseFloat(matcher.group(7));
                break;
            }
        }

        String vmstat = vmstatLines.get(vmstatLines.size() -1);
        String[] splitStr = vmstat.trim().split("\\s+");
        if(splitStr.length != 11) {
            throw new UnsupportedOperationException("vmstat string error: " + splitStr.length);
        }

        this.user = Float.parseFloat(splitStr[0]);
        this.sys = Float.parseFloat(splitStr[1]);
        this.wait = Float.parseFloat(splitStr[2]);
        this.idle = Float.parseFloat(splitStr[3]);
        this.physc = Float.parseFloat(splitStr[4]);
        this.entc = Float.parseFloat(splitStr[5]);
        this.lbusy = Float.parseFloat(splitStr[6]);

    }

    public Float getUser() {
        return user;
    }

    public Float getSys() {
        return sys;
    }

    public Float getIdle() {
        return idle;
    }

    public Float getWait() {
        return wait;
    }

    public Float getPhysc() {
        return physc;
    }

    public Float getEntc() {
        return entc;
    }

    public Float getLbusy() {
        return lbusy;
    }

    public float getUsage() {
        return 100 - idle;
    }

    public Map<String, String> getTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("mode", mode);
        tags.put("type", type);
        return tags;
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

        return fields;
    }
}
