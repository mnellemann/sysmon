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

    private HardwareAbstractionLayer hardwareAbstractionLayer;

    @Override
    public boolean isSupported() {
        hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return hardwareAbstractionLayer != null;
    }

    @Override
    public String getName() {
        return "base_network";
    }

    @Override
    public String getProvides() {
        return "network";
    }

    @Override
    public String getDescription() {
        return "Base Network Metrics";
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
        return new MetricResult(getName(), new Measurement(tagsMap, fieldsMap));
    }

}
