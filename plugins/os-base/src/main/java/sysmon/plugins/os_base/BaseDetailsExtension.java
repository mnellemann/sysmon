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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class BaseDetailsExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseDetailsExtension.class);

    // Extension details
    private final String name = "base_details";
    private final String provides = "details";
    private final String description = "Base Details Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;

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
        if (map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
    }

    @Override
    public MetricResult getMetrics() {

        HashMap<String, Object> fieldsMap = new HashMap<String, Object>() {{
            put("family", systemInfo.getOperatingSystem().getFamily());                         // Freedesktop.org      / AIX
            put("manufacturer", systemInfo.getOperatingSystem().getManufacturer());             // GNU/Linux            / IBM
            put("os_codename", systemInfo.getOperatingSystem().getVersionInfo().getCodeName()); // Flatpak runtime      / ppc64
            put("os_version", systemInfo.getOperatingSystem().getVersionInfo().getVersion());   // 21.08.4              / 7.2
            put("os_build", systemInfo.getOperatingSystem().getVersionInfo().getBuildNumber()); // 5.13.0-7620-generic  / 2045B_72V
            put("uptime", systemInfo.getOperatingSystem().getSystemUptime());
            put("threads", systemInfo.getOperatingSystem().getThreadCount());
        }};

        return new MetricResult(name, new Measurement(new HashMap<>(), fieldsMap));
    }

}