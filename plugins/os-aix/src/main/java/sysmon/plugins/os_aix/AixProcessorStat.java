package sysmon.plugins.os_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixProcessorStat {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorStat.class);

    // System configuration: type=Shared mode=Uncapped smt=8 lcpu=8 mem=4096MB psize=19 ent=0.50
    private static final Pattern patternAixShared = Pattern.compile("^System configuration: type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+)MB psize=(\\d+) ent=(\\d+\\.?\\d*)");

    // System configuration: type=Dedicated mode=Donating smt=8 lcpu=16 mem=4096MB
    private static final Pattern patternAixDedicated = Pattern.compile("^System configuration: type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+)MB");

    // type=Shared mode=Uncapped smt=8 lcpu=4 mem=4101120 kB cpus=24 ent=4.00
    private static final Pattern patternLinux = Pattern.compile("^type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+) kB cpus=(\\d+) ent=(\\d+\\.?\\d*)");


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


    public AixProcessorStat(InputStream inputStream) throws IOException {

        String lastLine = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) {
            String line = reader.readLine();

            if (line.startsWith("System configuration:")) {
                Matcher matcher = patternAixShared.matcher(line);
                if (matcher.find() && matcher.groupCount() == 7) {
                    type = matcher.group(1);
                    mode = matcher.group(2);
                    smt = Integer.parseInt(matcher.group(3));
                    lcpu = Integer.parseInt(matcher.group(4));
                    psize = Integer.parseInt(matcher.group(5));
                    ent = Float.parseFloat(matcher.group(7));
                }
                matcher = patternAixDedicated.matcher(line);
                if (matcher.find() && matcher.groupCount() == 5) {
                    type = matcher.group(1);
                    mode = matcher.group(2);
                    smt = Integer.parseInt(matcher.group(3));
                    lcpu = Integer.parseInt(matcher.group(4));
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

            lastLine = line;
        }

        //String lparstat = lines.get(lines.size() -1);
        String[] splitStr = Objects.requireNonNull(lastLine).trim().split("\\s+");
        if(type.equalsIgnoreCase("shared") && splitStr.length < 9 ||
                type.equalsIgnoreCase("dedicated") && splitStr.length < 8) {
            throw new UnsupportedOperationException("lparstat string error: " + lastLine);
        }

        this.user = Float.parseFloat(splitStr[0]);
        this.sys = Float.parseFloat(splitStr[1]);
        this.wait = Float.parseFloat(splitStr[2]);
        this.idle = Float.parseFloat(splitStr[3]);
        this.physc = Float.parseFloat(splitStr[4]);
        if(type.equalsIgnoreCase("shared")) {
            this.entc = Float.parseFloat(splitStr[5]);
            this.lbusy = Float.parseFloat(splitStr[6]);
        } else {
            this.entc = 0f;
            this.lbusy = 0f;
        }

        inputStream.close();
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

    public HashMap<String, String> getTags() {
        return new HashMap<>();
    }

    public HashMap<String, Object> getFields() {
        return new HashMap<String, Object>() {{
            put("lcpu", lcpu);
            put("ent", ent);
            put("user", user);
            put("sys", sys);
            put("idle", idle);
            put("wait", wait);
            put("physc", physc);
            put("entc", entc);
            put("lbusy", lbusy);
            put("mode", mode);
            put("type", type);
        }};
    }
}
