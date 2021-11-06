package sysmon.plugins.os_aix;

import org.pf4j.Extension;
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

@Extension
public class AixProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorExtension.class);

    // Extension details
    private final String name = "aix_processor";
    private final String provides = "lpar_processor";
    private final String description = "AIX Processor Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = true;


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

        if(!PluginHelper.canExecute("lparstat")) {
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

        HashMap<String, String> tagsMap = null;
        HashMap<String, Object> fieldsMap = null;

        try (InputStream buf = PluginHelper.executeCommand("lparstat 3 1")) {
            AixProcessorStat processorStat = processCommandOutput(buf);
            tagsMap = processorStat.getTags();
            fieldsMap = processorStat.getFields();
        } catch (IOException e) {
            log.error("lparstat error", e);
        }

        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }


    protected AixProcessorStat processCommandOutput(InputStream input) throws IOException {
        return new AixProcessorStat(input);
    }

}
