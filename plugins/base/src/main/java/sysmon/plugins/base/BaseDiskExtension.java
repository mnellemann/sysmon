package sysmon.plugins.base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.*;

@Extension
public class BaseDiskExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseDiskExtension.class);

    // Extension details
    private final String name = "base_disk";
    private final String description = "Base Disk Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "30s";

    private HardwareAbstractionLayer hardwareAbstractionLayer;
    private List<HWDiskStore> diskStores;
    private int refreshCounter = 0;


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

        ArrayList<Measurement> measurementList = new ArrayList<>();
        if(diskStores == null || refreshCounter++ > 360) {
            log.debug("getMetrics() - refreshing list of disk stores");
            diskStores = hardwareAbstractionLayer.getDiskStores();
            refreshCounter = 0;
        }

        for(HWDiskStore store : diskStores) {

            store.updateAttributes();
            String name = store.getName();
            if (name.matches("h?disk[0-9]+") ||
                //name.matches("/dev/dm-[0-9]+") ||
                name.matches("/dev/x?[sv]d[a-z]") ||
                name.matches("/dev/nvme[0-9]n[0-9]") ||
                name.startsWith("\\\\.\\PHYSICALDRIVE")
            ) {

                TreeMap<String, String> tagsMap = new TreeMap<String, String>() {{
                    put("name", name);
                }};

                TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
                    put("read", store.getReadBytes());
                    put("write", store.getWriteBytes());
                    put("iotime", store.getTransferTime());
                    put("queue", store.getCurrentQueueLength());
                }};

                log.debug("getMetrics() - tags: {}, fields: {}", tagsMap, fieldsMap);
                measurementList.add(new Measurement(tagsMap, fieldsMap));
            } else {
                log.debug("getMetrics() - skipping device: {}", name);
            }

        }

        return new MetricResult(name, measurementList);
    }

}
