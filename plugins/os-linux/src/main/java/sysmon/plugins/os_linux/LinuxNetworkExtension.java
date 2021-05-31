package sysmon.plugins.os_linux;

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
public class LinuxNetworkExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(LinuxNetworkExtension.class);

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
        return "linux-network-sockets";
    }

    @Override
    public String getProvides() {
        return "network-sockets";
    }

    @Override
    public String getDescription() {
        return "Linux Network Socket Metrics";
    }

    @Override
    public MetricResult getMetrics() {

        LinuxNetworkSockStat sockStat = processSockOutput(PluginHelper.readFile("/proc/net/sockstat"));

        Map<String, String> tagsMap = sockStat.getTags();
        Map<String, Object> fieldsMap = sockStat.getFields();

        log.debug(fieldsMap.toString());
        return new MetricResult("network_sockets", new Measurement(tagsMap, fieldsMap));

    }

    protected LinuxNetworkSockStat processSockOutput(List<String> inputLines) {
        return new LinuxNetworkSockStat(inputLines);
    }

}
