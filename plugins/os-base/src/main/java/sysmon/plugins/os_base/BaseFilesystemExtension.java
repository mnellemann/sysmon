package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class BaseFilesystemExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseDiskExtension.class);

    // Extension details
    private final String name = "base_filesystem";
    private final String provides = "filesystem";
    private final String description = "Base Filesystem Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private List<?> excludeType = new ArrayList<String>() {{
      add("tmpfs");
      add("ahafs");
    }};
    private List<?> excludeMount = new ArrayList<String>() {{
        add("/boot/efi");
    }};

    private HardwareAbstractionLayer hardwareAbstractionLayer;
    private SystemInfo systemInfo;


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
        return null;
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
        List<OSFileStore> fileStores = systemInfo.getOperatingSystem().getFileSystem().getFileStores(true);

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

            HashMap<String, String> tagsMap = new HashMap<String, String>() {{
                put("name", name);
                put("type", type);
                put("mount", mount);
            }};

            HashMap<String, Object> fieldsMap = new HashMap<String, Object>() {{
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
