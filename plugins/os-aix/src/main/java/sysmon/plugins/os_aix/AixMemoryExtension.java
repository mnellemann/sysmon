package sysmon.plugins.os_aix;

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
public class AixMemoryExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixMemoryExtension.class);

    @Override
    public boolean isSupported() {

        if(!System.getProperty("os.name").toLowerCase().contains("aix")) {
            log.warn("Requires AIX.");
            return false;
        }

        if(!PluginHelper.canExecute("svmon")) {
            log.warn("Requires the 'svmon' command.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "aix-memory";
    }

    @Override
    public String getProvides() {
        return "memory";
    }

    @Override
    public String getDescription() {
        return "AIX Memory Metrics";
    }

    @Override
    public MetricResult getMetrics() {

        //List<String> svmon = PluginHelper.executeCommand("svmon -G -O unit=KB");
        List<String> svmon = PluginHelper.executeCommand("svmon -G -O summary=longreal,unit=KB");
        AixMemoryStat memoryStat = processCommandOutput(svmon);

        Map<String, String> tagsMap = memoryStat.getTags();
        Map<String, Object> fieldsMap = memoryStat.getFields();

        return new MetricResult("memory", new Measurement(tagsMap, fieldsMap));
    }

    protected AixMemoryStat processCommandOutput(List<String> inputLines) {
        return new AixMemoryStat(inputLines);
    }


}
