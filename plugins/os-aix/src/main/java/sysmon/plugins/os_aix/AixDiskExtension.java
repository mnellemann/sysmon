package sysmon.plugins.os_aix;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.util.List;
import java.util.Map;


@Extension
public class AixDiskExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorExtension.class);

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardwareAbstractionLayer;

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


    public AixDiskExtension() {

        systemInfo = new SystemInfo();
        hardwareAbstractionLayer = systemInfo.getHardware();

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

        long writeBytes = hardwareAbstractionLayer.getDiskStores().get(0).getWriteBytes();
        log.warn(String.format("Disk 0 - Write Bytes: %d", writeBytes));

        long readBytes = hardwareAbstractionLayer.getDiskStores().get(0).getReadBytes();
        log.warn(String.format("Disk 0 - Read Bytes: %d", readBytes));

        long memAvailable = hardwareAbstractionLayer.getMemory().getAvailable();
        log.warn(String.format("Memory - Available: %d", memAvailable));

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
