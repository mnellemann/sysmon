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

// Disabled
//@Extension
public class AixNetstatExtension implements MetricExtension  {

    private static final Logger log = LoggerFactory.getLogger(AixNetstatExtension.class);

    @Override
    public boolean isSupported() {

        if(!System.getProperty("os.name").toLowerCase().contains("aix")) {
            log.warn("Requires AIX.");
            return false;
        }

        if(!PluginHelper.canExecute("netstat")) {
            log.warn("Requires the 'netstat' command.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "aix_network_netstat";
    }

    @Override
    public String getProvides() {
        return "network_netstat";
    }

    @Override
    public String getDescription() {
        return "AIX Netstat Metrics";
    }

    @Override
    public MetricResult getMetrics() throws Exception {

        HashMap<String, String> tagsMap = null;
        HashMap<String, Object> fieldsMap = null;

        try (InputStream buf = PluginHelper.executeCommand("netstat -s -f inet")) {
            AixNetstatParser parser = processCommandOutput(buf);
            tagsMap = parser.getTags();
            fieldsMap = parser.getFields();
        }

        log.debug(fieldsMap.toString());
        return new MetricResult(getName(), new Measurement(tagsMap, fieldsMap));
    }


    protected AixNetstatParser processCommandOutput(InputStream input) throws IOException {
        return new AixNetstatParser(input);
    }

}

