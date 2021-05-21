package sysmon.plugins.os_linux;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.util.List;
import java.util.Map;

@Extension
public class LinuxMemoryExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(LinuxMemoryExtension.class);

    @Override
    public boolean isSupported() {

        if(!System.getProperty("os.name").toLowerCase().contains("linux")) {
            log.warn("Requires Linux.");
            return false;
        }

        if(!PluginHelper.canExecute("free")) {
            log.warn("Requires the 'free' command.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "linux-memory";
    }

    @Override
    public String getProvides() {
        return "memory";
    }

    @Override
    public String getDescription() {
        return "Linux Memory Metrics";
    }

    @Override
    public MetricResult getMetrics() {

        List<String> svmon = PluginHelper.executeCommand("free -k");
        LinuxMemoryStat memoryStat = processCommandOutput(svmon);

        Map<String, String> tagsMap = memoryStat.getTags();
        Map<String, Object> fieldsMap = memoryStat.getFields();

        return new MetricResult("memory", new Measurement(tagsMap, fieldsMap));
    }

    protected LinuxMemoryStat processCommandOutput(List<String> inputLines) {
        return new LinuxMemoryStat(inputLines);
    }


}
