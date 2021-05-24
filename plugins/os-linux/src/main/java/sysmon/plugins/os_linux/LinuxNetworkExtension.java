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
        return "linux-network";
    }

    @Override
    public String getProvides() {
        return "network";
    }

    @Override
    public String getDescription() {
        return "Linux Network Metrics";
    }

    @Override
    public MetricResult getMetrics() {

        // LinuxNetworkDevStat = 2 x reading from /proc/net/dev ?
        LinuxNetworkDevProcLine proc1 = processDevOutput(PluginHelper.readFile("/proc/net/dev"));
        try {
            Thread.sleep(1 * 1000); // TODO: Configure sample collect time
        } catch (InterruptedException e) {
            log.warn("getMetrics() - sleep interrupted");
            return null;
        }
        LinuxNetworkDevProcLine proc2 = processDevOutput(PluginHelper.readFile("/proc/net/dev"));

        // LinuxNetworkSockStat = 1 x reading from /proc/net/sockstats
        LinuxNetworkSockStat stat = processSockOutput(PluginHelper.readFile("/proc/net/sockstat"));


        Map<String, String> tagsMap = stat.getTags();
        Map<String, Object> fieldsMap = stat.getFields();


        return new MetricResult("network", new Measurement(tagsMap, fieldsMap));
    }

    protected LinuxNetworkSockStat processSockOutput(List<String> inputLines) {
        return new LinuxNetworkSockStat(inputLines);
    }

    protected LinuxNetworkDevProcLine processDevOutput(List<String> inputLines) {
        return new LinuxNetworkDevProcLine(inputLines);
    }


}
