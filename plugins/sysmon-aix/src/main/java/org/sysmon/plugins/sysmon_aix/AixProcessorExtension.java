package org.sysmon.plugins.sysmon_aix;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.Measurement;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricResult;
import org.sysmon.shared.PluginHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class AixProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorExtension.class);

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("aix");
    }

    @Override
    public String getName() {
        return "aix-processor";
    }

    @Override
    public String getDescription() {
        return "AIX Processor Metrics";
    }

    @Override
    public MetricResult getMetrics() {

        MetricResult result = new MetricResult("processor");
        List<Measurement> measurementList = new ArrayList<>();

        List<String> mpstat = PluginHelper.executeCommand("mpstat", "-a");
        List<AixProcessorStat> processorStats = processCommandOutput(mpstat);

        for(AixProcessorStat stat : processorStats) {

            Map<String, String> tagsMap = new HashMap<>();
            tagsMap.put("cpu", stat.getName());
            // TODO: entitlements as tag or field ?

            Map<String, Object> fieldsMap = new HashMap<>();
            fieldsMap.put("utilization", stat.getUtilizationPercentage());

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }
        result.addMeasurements(measurementList);

        return result;
    }


    protected List<AixProcessorStat> processCommandOutput(List<String> inputLines) {
        List<AixProcessorStat> processorStatList = new ArrayList<>();

        for(String line : inputLines) {
            if(line.matches("^\\s+[0-9]+\\s+.*")) {
                processorStatList.add(new AixProcessorStat(line));
            }
        }

        return processorStatList;
    }


}
