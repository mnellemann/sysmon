package sysmon.plugins.base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Extension
public class BasePowerExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BasePowerExtension.class);

    // Extension details
    private final String name = "base_power";
    private final String description = "Base Power Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "30s";

    private HardwareAbstractionLayer hardwareAbstractionLayer;
    private List<PowerSource> powerSources;

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
        if (map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if (map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
    }

    @Override
    public MetricResult getMetrics() {

        if(powerSources == null) {
            powerSources = hardwareAbstractionLayer.getPowerSources();
        }

        ArrayList<Measurement> measurementList = new ArrayList<>();
        powerSources.forEach((source) -> {
            log.info("name: {}", source.getName());
            log.info("amp: {}", source.getAmperage());
            log.info("voltage: {}", source.getVoltage());

            TreeMap<String, String> tagsMap = new TreeMap<String, String>() {
                {
                    put("name", source.getName());
                }
            };

            TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {
                {
                    put("amperage", source.getAmperage());
                    put("voltage", source.getVoltage());
                }
            };

            log.debug("getMetrics() - tags: {}, fields: {}", tagsMap, fieldsMap);
            measurementList.add(new Measurement(tagsMap, fieldsMap));

        });

        return new MetricResult(name, measurementList);
    }

}
