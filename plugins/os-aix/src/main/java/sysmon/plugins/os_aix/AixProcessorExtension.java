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
import java.util.List;
import java.util.Map;

@Extension
public class AixProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(AixProcessorExtension.class);

    @Override
    public boolean isSupported() {

        String osArch = System.getProperty("os.arch").toLowerCase();
        if(!osArch.startsWith("ppc64")) {
            log.warn("Requires CPU Architecture ppc64 or ppc64le, this is: " + osArch);
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
        return "aix-processor";
    }

    @Override
    public String getProvides() {
        return "processor-lpar";
    }

    @Override
    public String getDescription() {
        return "AIX Processor Metrics";
    }

    @Override
    public MetricResult getMetrics() throws Exception {

        Map<String, String> tagsMap = null;
        Map<String, Object> fieldsMap = null;

        try (InputStream buf = PluginHelper.executeCommand("lparstat 5 1")) {
            AixProcessorStat processorStat = processCommandOutput(buf);
            tagsMap = processorStat.getTags();
            fieldsMap = processorStat.getFields();
        }

        return new MetricResult("processor_lpar", new Measurement(tagsMap, fieldsMap));
    }


    protected AixProcessorStat processCommandOutput(InputStream input) throws IOException {
        return new AixProcessorStat(input);
    }


}
