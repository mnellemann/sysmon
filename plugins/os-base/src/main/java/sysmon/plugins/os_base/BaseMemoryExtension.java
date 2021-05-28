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

    private SystemInfo systemInfo;
    private HardwareAbstractionLayer hardwareAbstractionLayer;

    @Override
    public boolean isSupported() {

        try {
            systemInfo = new SystemInfo();
            hardwareAbstractionLayer = systemInfo.getHardware();
            return true;
        } catch (UnsupportedOperationException e) {
            log.warn(e.getMessage());
        }

        return false;
    }

    @Override
    public String getName() {
        return "base-memory";
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

        Map<String, String> tagsMap = new HashMap<>();
        Map<String, Object> fieldsMap = new HashMap<>();

        long total = hardwareAbstractionLayer.getMemory().getTotal();
        long available = hardwareAbstractionLayer.getMemory().getAvailable();
        float usage = ((float) (total - available)  / total ) * 100;

        fieldsMap.put("available", available);
        fieldsMap.put("total", total);
        fieldsMap.put("usage", usage);
        fieldsMap.put("paged", hardwareAbstractionLayer.getMemory().getPageSize());
        fieldsMap.put("virtual", hardwareAbstractionLayer.getMemory().getVirtualMemory().getVirtualInUse());

        return new MetricResult("memory", new Measurement(tagsMap, fieldsMap));
    }


}
