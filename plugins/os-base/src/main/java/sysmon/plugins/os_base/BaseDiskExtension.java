package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class BaseDiskExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseDiskExtension.class);

    // Extension details
    private final String name = "base_disk";
    private final String provides = "disk";
    private final String description = "Base Disk Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;

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
        if(map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
    }

    @Override
    public MetricResult getMetrics() {

        ArrayList<Measurement> measurementList = new ArrayList<>();
        List<HWDiskStore> diskStores = hardwareAbstractionLayer.getDiskStores();

        for(HWDiskStore store : diskStores) {

            String name = store.getName();
            if (name.matches("h?disk[0-9]+") || name.matches("/dev/x?[sv]d[a-z]") || name.matches("/dev/nvme[0-9]n[0-9]")) {

                HashMap<String, String> tagsMap = new HashMap<String, String>() {{
                    put("name", name);
                }};

                HashMap<String, Object> fieldsMap = new HashMap<String, Object>() {{
                    put("read", store.getReadBytes());
                    put("write", store.getWriteBytes());
                    put("iotime", store.getTransferTime());
                    put("queue", store.getCurrentQueueLength());
                }};

                measurementList.add(new Measurement(tagsMap, fieldsMap));
            }

        }

        return new MetricResult(name, measurementList);
    }

}