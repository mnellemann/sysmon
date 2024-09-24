package sysmon.plugins.unix;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

@Extension
public class UnixVmstatExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(UnixVmstatExtension.class);

    // Extension details
    private final String name = "unix_vmstat";
    private final String description = "UNIX VMStat Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = true;
    private String interval = "30s";

    protected String osType = "unknown";

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

        SystemInfo systemInfo = UnixPlugin.getSystemInfo();
        if(systemInfo == null) {
            return false;
        }

        if(systemInfo.getOperatingSystem().getManufacturer().equals("GNU/Linux")) {
            osType = "linux";
        }
        if(systemInfo.getOperatingSystem().getManufacturer().equals("IBM") && systemInfo.getOperatingSystem().getFamily().equals("AIX")) {
            osType = "aix";
        }

        if(PluginHelper.notExecutable("vmstat")) {
            log.warn("Requires the 'vmstat' command.");
            return false;
        }

        return true;
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
    public MetricResult getMetrics() throws Exception {

        TreeMap<String, String> tagsMap = null;
        TreeMap<String, Object> fieldsMap = null;

        try (InputStream buf = PluginHelper.executeCommand("vmstat -w 1 1")) {
            UnixVmstatOutput vmstatOutput = processCommandOutput(buf);
            tagsMap = vmstatOutput.getTags();
            fieldsMap = vmstatOutput.getFields();
        } catch (IOException e) {
            log.error("vmstat error", e);
        }

        log.info("getMetrics() - tags: {}, fields: {}", tagsMap, fieldsMap);
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }


    protected UnixVmstatOutput processCommandOutput(InputStream input) throws IOException {
        return new UnixVmstatOutput(osType, input);
    }

}
