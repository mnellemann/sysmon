package sysmon.plugins.power;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

@Extension
public class PowerProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(PowerProcessorExtension.class);

    // Extension details
    private final String name = "power_processor";
    private final String description = "IBM Power Processor Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = true;
    private String interval = "10s";

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

        String osArch = System.getProperty("os.arch").toLowerCase();
        if(!osArch.startsWith("ppc64")) {
            log.debug("Requires CPU Architecture ppc64 or ppc64le, this is: " + osArch);
            return false;
        }

        if(PluginHelper.notExecutable("lparstat")) {
            log.warn("Requires the 'lparstat' command.");
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

        try (InputStream buf = PluginHelper.executeCommand("lparstat 3 1")) {
            PowerProcessorStat processorStat = processCommandOutput(buf);
            tagsMap = processorStat.getTags();
            fieldsMap = processorStat.getFields();
        } catch (IOException e) {
            log.error("lparstat error", e);
        }

        log.debug("getMetrics() - tags: {}, fields: {}", tagsMap, fieldsMap);
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }


    protected PowerProcessorStat processCommandOutput(InputStream input) throws IOException {
        return new PowerProcessorStat(input);
    }

}
