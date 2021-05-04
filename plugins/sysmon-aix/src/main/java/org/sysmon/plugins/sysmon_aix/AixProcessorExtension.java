package org.sysmon.plugins.sysmon_aix;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricMeasurement;
import org.sysmon.shared.MetricResult;
import org.sysmon.shared.PluginHelper;

import java.util.ArrayList;
import java.util.List;

@Extension
public class AixProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorExtension.class);

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("aix");
    }

    @Override
    public String getGreeting() {
        return "Welcome from AIX ProcessorMetric";
    }

    @Override
    public MetricResult getMetrics() {

        MetricResult result = new MetricResult("processor");

        List<String> mpstat = PluginHelper.executeCommand("mpstat", "-a");
        List<AixProcessorStat> processorStats = processCommandOutput(mpstat);
        for(AixProcessorStat stat : processorStats) {
            result.addMetricMeasurement(new MetricMeasurement(String.format("cpu%d", stat.getCpuNum()), stat.getUtilizationPercentage()));
        }

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
