package sysmon.plugins.os_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AixProcessorStat {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorStat.class);

    // System configuration: type=Shared mode=Uncapped smt=8 lcpu=8 mem=4096MB psize=19 ent=0.50
    private static final Pattern patternAixShared = Pattern.compile("^System configuration: type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+)MB psize=(\\d+) ent=(\\d+\\.?\\d*)");

    // System configuration: type=Dedicated mode=Capped smt=4 lcpu=12 mem=24576MB
    // System configuration: type=Dedicated mode=Donating smt=8 lcpu=16 mem=4096MB
    private static final Pattern patternAixDedicated = Pattern.compile("^System configuration: type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+)MB");


    // type=Shared mode=Uncapped smt=8 lcpu=4 mem=4101120 kB cpus=24 ent=4.00
    private static final Pattern patternLinux = Pattern.compile("^type=(\\S+) mode=(\\S+) smt=(\\d+) lcpu=(\\d+) mem=(\\d+) kB cpus=(\\d+) ent=(\\d+\\.?\\d*)");



    private String type;        // Indicates the partition type. The value can be either dedicated or shared.
    private String mode;        // Indicates whether the partition processor capacity is capped uncapped.
    private int smt;            // Indicates whether simultaneous multithreading is enabled or disabled in the partition.
    private int lcpu;           // Indicates the number of online logical processors.
    //private int psize;          // Indicates the number of online physical processors in the pool.
    private float ent;          // Indicates the entitled processing capacity in processor units (shared mode only).

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
            log.trace("AixProcessorStat() - {}", line);

            if (line.startsWith("System configuration:")) {
                Matcher matcher = patternAixShared.matcher(line);
                if (matcher.find() && matcher.groupCount() == 7) {
                    type = matcher.group(1);
                    mode = matcher.group(2);
                    smt = Integer.parseInt(matcher.group(3));
                    lcpu = Integer.parseInt(matcher.group(4));
                    //psize = Integer.parseInt(matcher.group(6));
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
                    //psize = Integer.parseInt(matcher.group(4));
                    lcpu = Integer.parseInt(matcher.group(4));
                    ent = Float.parseFloat(matcher.group(7));
                }
            }

            lastLine = line;
        }

        //String lparstat = lines.get(lines.size() -1);
        String[] splitStr = Objects.requireNonNull(lastLine).trim().split("\\s+");
        if(type == null ||
                (mode.equalsIgnoreCase("Capped") && splitStr.length < 4) ||
                (type.equalsIgnoreCase("Shared") && splitStr.length < 9) ||
                (type.equalsIgnoreCase("Dedicated") && mode.equalsIgnoreCase("Donating") && splitStr.length < 8)
        ) {
            log.error("lparstat parse error - mode: {}, type: {}, content: {}", mode, type, Arrays.toString(splitStr));
            throw new UnsupportedOperationException("lparstat parse error.");
        }

        this.user = Float.parseFloat(splitStr[0]);
        this.sys = Float.parseFloat(splitStr[1]);
        this.wait = Float.parseFloat(splitStr[2]);
        this.idle = Float.parseFloat(splitStr[3]);

        if(mode.equalsIgnoreCase("Uncapped") || mode.equalsIgnoreCase("Donating")) {
            this.physc = Float.parseFloat(splitStr[4]);
        } else {
            this.physc = 0f;
        }

        if(type.equalsIgnoreCase("Shared")) {
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
            put("smt", smt);
        }};
    }
}
