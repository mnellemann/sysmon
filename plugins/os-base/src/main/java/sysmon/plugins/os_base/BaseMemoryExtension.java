package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.HashMap;
import java.util.Map;

@Extension
public class BaseMemoryExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseMemoryExtension.class);

    private HardwareAbstractionLayer hardwareAbstractionLayer;

    @Override
    public boolean isSupported() {
        hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return hardwareAbstractionLayer != null;
    }

    @Override
    public String getName() {
        return "base_memory";
    }

    @Override
    public String getProvides() {
        return "memory";
    }

    @Override
    public String getDescription() {
        return "Base Memory Metrics";
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
        return new MetricResult(getName(), new Measurement(tagsMap, fieldsMap));
    }


}
