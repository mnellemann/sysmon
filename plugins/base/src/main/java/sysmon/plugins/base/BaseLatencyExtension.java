package sysmon.plugins.base;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.pf4j.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

@Extension
public class BaseLatencyExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseLatencyExtension.class);

    private final int sleepTimeMillis = 5000;

    // Extension details
    private final String name = "base_latency";
    private final String description = "Base Latency Information";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = true;
    private String interval = "30s";
    private final HashMap<String, String> tags = new HashMap<>();


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isThreaded() {
        return threaded;
    }

    @Override
    public boolean isSupported() {
        SystemInfo systemInfo = BasePlugin.getSystemInfo();
        return systemInfo != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInterval() { return interval; }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if (map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
        if (map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if (map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
    }

    @Override
    public MetricResult getMetrics() {

        // TODO: Calculate execution time for sysmon-client to monitor drift
        // Available from Java 9
        //long pid = ProcessHandle.current().pid();

        // Calculate sleep-overrun in milliseconds
        long sleepOverrun = 0;
        try {
            long nanoTime1 = System.nanoTime();
            Thread.sleep(sleepTimeMillis);
            long nanoTime2 = System.nanoTime();
            long nanoDiff = nanoTime2 - nanoTime1;
            sleepOverrun = (nanoDiff / 1_000_000)  - sleepTimeMillis;
            log.warn("Sleep Overrun: {}", sleepOverrun);
        } catch (InterruptedException e) {
            log.error("getMetrics() - error: {}", e.getMessage());
        }

        long finalSleepOverrun = sleepOverrun;
        TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
            put("overrun", finalSleepOverrun);
        }};
        return new MetricResult(name, new Measurement(tags, fieldsMap));

    }

}
