package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class BaseNetworkExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseNetworkExtension.class);

    // Extension details
    private final String name = "base_network";
    private final String provides = "network";
    private final String description = "Base Network Metrics";

    // Configuration / Options
    private boolean enabled = true;


    private HardwareAbstractionLayer hardwareAbstractionLayer;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isSupported() {
        hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return hardwareAbstractionLayer != null;
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

        long rxBytes = 0L;
        long rxPackets = 0L;
        long rxErrs = 0L;
        long txBytes = 0L;
        long txPackets = 0L;
        long txErrs = 0L;

        HashMap<String, String> tagsMap = new HashMap<>();
        HashMap<String, Object> fieldsMap = new HashMap<>();

        List<NetworkIF> interfaces = hardwareAbstractionLayer.getNetworkIFs();
        for(NetworkIF netif : interfaces) {
            //String name = netif.getName();
            //log.warn("Device: " + name);
            rxPackets += netif.getPacketsRecv();
            txPackets += netif.getPacketsSent();
            rxBytes += netif.getBytesRecv();
            txBytes += netif.getBytesSent();
            rxErrs += netif.getInErrors();
            txErrs += netif.getOutErrors();
        }

        fieldsMap.put("rxPackets", rxPackets);
        fieldsMap.put("txPackets", txPackets);
        fieldsMap.put("rxBytes", rxBytes);
        fieldsMap.put("txBytes", txBytes);
        fieldsMap.put("rxErrors", rxErrs);
        fieldsMap.put("txErrors", txErrs);

        log.debug(fieldsMap.toString());
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }

}
