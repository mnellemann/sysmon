package sysmon.plugins.base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.*;

@Extension
public class BaseFilesystemExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseDiskExtension.class);

    // Extension details
    private final String name = "base_filesystem";
    private final String description = "Base Filesystem Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "10s";
    private List<?> excludeType = new ArrayList<String>() {{
      add("tmpfs");
      add("ahafs");
    }};
    private List<?> excludeMount = new ArrayList<String>() {{
        add("/boot/efi");
    }};

    private SystemInfo systemInfo;
    private List<OSFileStore> fileStores;
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
        systemInfo = BasePlugin.getSystemInfo();
        //hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return systemInfo != null;
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

        if(map.containsKey("exclude_type")) {
            excludeType = (List<?>) map.get("exclude_type");
        }

        if(map.containsKey("exclude_mount")) {
            excludeMount = (List<?>) map.get("exclude_mount");
        }
    }

    @Override
    public MetricResult getMetrics() {

        ArrayList<String> alreadyProcessed = new ArrayList<>();
        ArrayList<Measurement> measurementList = new ArrayList<>();

        if(fileStores == null || refreshCounter++ > 360) {
            fileStores = systemInfo.getOperatingSystem().getFileSystem().getFileStores(true);
        }

        for(OSFileStore store : fileStores) {

            String name = store.getName();
            String type = store.getType();
            String mount = store.getMount();

            if(excludeType.contains(type)) {
                log.debug("Excluding type: " + type);
                continue;
            }

            if(excludeMount.contains(mount)) {
                log.debug("Excluding mount: " + mount);
                continue;
            }

            if(alreadyProcessed.contains(name)) {
                log.debug("Skipping name: " + name);
                continue;
            }

            alreadyProcessed.add(name);
            store.updateAttributes();

            TreeMap<String, String> tagsMap = new TreeMap<String, String>() {{
                put("name", name);
                put("type", type);
                put("mount", mount);
            }};

            TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
                put("free_bytes", store.getFreeSpace());
                put("total_bytes", store.getTotalSpace());
                put("free_inodes", store.getFreeInodes());
                put("total_inodes", store.getTotalInodes());
            }};

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }

        return new MetricResult(name, measurementList);
    }

}
