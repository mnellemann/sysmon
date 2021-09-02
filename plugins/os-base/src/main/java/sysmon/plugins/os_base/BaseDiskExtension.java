package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class BaseDiskExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseDiskExtension.class);

    private HardwareAbstractionLayer hardwareAbstractionLayer;


    @Override
    public boolean isSupported() {
        hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return hardwareAbstractionLayer != null;
    }

    @Override
    public String getName() {
        return "base_disk";
    }

    @Override
    public String getProvides() {
        return "disk";
    }

    @Override
    public String getDescription() {
        return "Base Disk Metrics";
    }


    @Override
    public MetricResult getMetrics() {

        long writeBytes = 0L;
        long readBytes = 0L;
        long transferTime = 0L;
        long queueLength = 0L;

        HashMap<String, String> tagsMap = new HashMap<>();
        HashMap<String, Object> fieldsMap = new HashMap<>();

        List<HWDiskStore> diskStores = hardwareAbstractionLayer.getDiskStores();
        for(HWDiskStore store : diskStores) {
            String name = store.getName();
            if (name.matches("hdisk[0-9]+") || name.matches("/dev/x?[sv]d[a-z]{1}") || name.matches("/dev/nvme[0-9]n[0-9]")) {
                log.debug("Using device: " + name);
                writeBytes += store.getWriteBytes();
                readBytes += store.getReadBytes();
                transferTime += store.getTransferTime();
                queueLength = store.getCurrentQueueLength();
            }
        }

        fieldsMap.put("reads", readBytes);
        fieldsMap.put("writes", writeBytes);
        fieldsMap.put("iotime", transferTime);
        fieldsMap.put("queue", queueLength);

        log.debug(fieldsMap.toString());
        return new MetricResult(getName(), new Measurement(tagsMap, fieldsMap));
    }

}