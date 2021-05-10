package org.sysmon.plugins.sysmon_aix;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.Measurement;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricResult;
import org.sysmon.shared.PluginHelper;

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

        List<String> vmstat = PluginHelper.executeCommand("/usr/bin/lparstat 1 1");
        AixProcessorStat processorStat = processCommandOutput(vmstat);

        Map<String, String> tagsMap = processorStat.getTags();
        Map<String, Object> fieldsMap = processorStat.getFields();

        return new MetricResult("processor", new Measurement(tagsMap, fieldsMap));
    }


    protected AixProcessorStat processCommandOutput(List<String> inputLines) {
        return new AixProcessorStat(inputLines);
    }


}
