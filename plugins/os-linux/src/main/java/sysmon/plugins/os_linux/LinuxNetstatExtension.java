package sysmon.plugins.os_linux;

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
public class LinuxNetstatExtension implements MetricExtension  {

    private static final Logger log = LoggerFactory.getLogger(LinuxNetstatExtension.class);

    @Override
    public boolean isSupported() {

        if(!System.getProperty("os.name").toLowerCase().contains("linux")) {
            log.warn("Requires Linux.");
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
        return "linux_network_netstat";
    }

    @Override
    public String getProvides() {
        return "network_netstat";
    }

    @Override
    public String getDescription() {
        return "Linux Netstat Metrics";
    }

    @Override
    public MetricResult getMetrics() throws Exception {

        HashMap<String, String> tagsMap = null;
        HashMap<String, Object> fieldsMap = null;

        try (InputStream inputStream = PluginHelper.executeCommand("netstat -s")) {
            LinuxNetstatParser parser = processCommandOutput(inputStream);
            tagsMap = parser.getTags();
            fieldsMap = parser.getFields();
        }

        log.debug(fieldsMap.toString());
        return new MetricResult(getName(), new Measurement(tagsMap, fieldsMap));
    }


    protected LinuxNetstatParser processCommandOutput(InputStream input) throws IOException {
        return new LinuxNetstatParser(input);
    }

}

