package sysmon.plugins.base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.Map;
import java.util.TreeMap;

@Extension
public class BaseLoadExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseLoadExtension.class);

    // Extension details
    private final String name = "base_load";
    private final String description = "Base Load Average Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "30s";

    private HardwareAbstractionLayer hardwareAbstractionLayer;

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
        hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return hardwareAbstractionLayer != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInterval() {
        return interval;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if (map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
        if(map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if(map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
    }

    @Override
    public MetricResult getMetrics() {

        double[] loadAvg = hardwareAbstractionLayer.getProcessor().getSystemLoadAverage(3);
        TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
            put("1min", loadAvg[0]);
            put("5min", loadAvg[1]);
            put("15min", loadAvg[2]);
        }};

        log.debug(fieldsMap.toString());
        return new MetricResult(name, new Measurement(new TreeMap<>(), fieldsMap));
    }

}
