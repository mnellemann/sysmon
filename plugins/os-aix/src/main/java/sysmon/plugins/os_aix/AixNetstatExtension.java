package sysmon.plugins.os_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

// Disabled
//@Extension
public class AixNetstatExtension implements MetricExtension  {

    private static final Logger log = LoggerFactory.getLogger(AixNetstatExtension.class);

    // Extension details
    private final String name = "aix_network_netstat";
    private final String provides = "network_netstat";
    private final String description = "AIX Netstat Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;


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

        if(!System.getProperty("os.name").toLowerCase().contains("aix")) {
            log.warn("Requires AIX.");
            return false;
        }

        if(PluginHelper.notExecutable("netstat")) {
            log.warn("Requires the 'netstat' command.");
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
    }

    @Override
    public MetricResult getMetrics() throws Exception {

        HashMap<String, String> tagsMap;
        HashMap<String, Object> fieldsMap;

        try (InputStream buf = PluginHelper.executeCommand("netstat -s -f inet")) {
            AixNetstatParser parser = processCommandOutput(buf);
            tagsMap = parser.getTags();
            fieldsMap = parser.getFields();
        }

        log.debug(fieldsMap.toString());
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }


    protected AixNetstatParser processCommandOutput(InputStream input) throws IOException {
        return new AixNetstatParser(input);
    }

}

