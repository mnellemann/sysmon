package sysmon.plugins.os_linux;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class LinuxSockstatExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(LinuxSockstatExtension.class);

    // Extension details
    private final String name = "linux_network_sockets";
    private final String provides = "network_sockets";
    private final String description = "Linux Network Socket Metrics";

    // Configuration / Options
    private boolean enabled = true;


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isSupported() {

        if(!System.getProperty("os.name").toLowerCase().contains("linux")) {
            log.warn("Requires Linux.");
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvides() {
        return provides;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if (map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
    }

    @Override
    public MetricResult getMetrics() {

        LinuxNetworkSockStat sockStat = processSockOutput(PluginHelper.readFile("/proc/net/sockstat"));

        HashMap<String, String> tagsMap = sockStat.getTags();
        HashMap<String, Object> fieldsMap = sockStat.getFields();

        log.debug(fieldsMap.toString());
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));

    }

    protected LinuxNetworkSockStat processSockOutput(List<String> inputLines) {
        return new LinuxNetworkSockStat(inputLines);
    }

}
