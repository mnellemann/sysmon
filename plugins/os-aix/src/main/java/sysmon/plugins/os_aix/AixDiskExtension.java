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
public class AixDiskExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorExtension.class);

    @Override
    public boolean isSupported() {

        if(!System.getProperty("os.name").toLowerCase().contains("aix")) {
            log.warn("Requires AIX.");
            return false;
        }

        if(!PluginHelper.canExecute("iostat")) {
            log.warn("Requires the 'iostat' command.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "aix-disk";
    }

    @Override
    public String getProvides() {
        return "disk";
    }

    @Override
    public String getDescription() {
        return "AIX Disk Metrics";
    }

    @Override
    public MetricResult getMetrics() {

        List<String> iostat = PluginHelper.executeCommand("iostat -d 1 1");
        AixDiskStat diskStat = processCommandOutput(iostat);

        Map<String, String> tagsMap = diskStat.getTags();
        Map<String, Object> fieldsMap = diskStat.getFields();

        return new MetricResult("disk", new Measurement(tagsMap, fieldsMap));
    }


    protected AixDiskStat processCommandOutput(List<String> inputLines) {
        return new AixDiskStat(inputLines);
    }


}
