package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.HashMap;
import java.util.Map;

@Extension
public class BaseMemoryExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseMemoryExtension.class);

    // Extension details
    private final String name = "base_memory";
    private final String provides = "memory";
    private final String description = "Base Memory Metrics";

    // Configuration / Options
    private boolean enabled = true;

    private HardwareAbstractionLayer hardwareAbstractionLayer;


    @Override
    public boolean isEnabled() {
        return enabled;
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
    public String getProvides() {
        return provides;
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
    }

    @Override
    public MetricResult getMetrics() {

        HashMap<String, String> tagsMap = new HashMap<>();
        HashMap<String, Object> fieldsMap = new HashMap<>();

        long total = hardwareAbstractionLayer.getMemory().getTotal();
        long available = hardwareAbstractionLayer.getMemory().getAvailable();
        float usage = ((float) (total - available)  / total ) * 100;

        fieldsMap.put("available", available);
        fieldsMap.put("total", total);
        fieldsMap.put("usage", usage);
        fieldsMap.put("paged", hardwareAbstractionLayer.getMemory().getPageSize());
        fieldsMap.put("virtual", hardwareAbstractionLayer.getMemory().getVirtualMemory().getVirtualInUse());

        log.debug(fieldsMap.toString());
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }


}
