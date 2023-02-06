package sysmon.plugins.base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Extension
public class BaseInfoExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseInfoExtension.class);

    // Extension details
    private final String name = "base_info";
    private final String description = "Base System Information";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "60m";
    private HashMap<String, String> tags = new HashMap<>();

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
        return systemInfo != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInterval() { return interval; }

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

        TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
            put("os_manufacturer", systemInfo.getOperatingSystem().getManufacturer());             // GNU/Linux            / IBM
            put("os_family", systemInfo.getOperatingSystem().getFamily());                         // Freedesktop.org      / AIX
            put("os_codename", systemInfo.getOperatingSystem().getVersionInfo().getCodeName()); // Flatpak runtime      / ppc64
            put("os_version", systemInfo.getOperatingSystem().getVersionInfo().getVersion());   // 21.08.4              / 7.2
            put("os_build", systemInfo.getOperatingSystem().getVersionInfo().getBuildNumber()); // 5.13.0-7620-generic  / 2045B_72V
            put("boot_time", systemInfo.getOperatingSystem().getSystemBootTime());
        }};

        return new MetricResult(name, new Measurement(tags, fieldsMap));
    }

}
