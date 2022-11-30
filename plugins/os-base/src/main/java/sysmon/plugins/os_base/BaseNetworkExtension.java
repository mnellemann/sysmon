package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.*;

@Extension
public class BaseNetworkExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseNetworkExtension.class);

    // Extension details
    private final String name = "base_network";
    private final String provides = "network";
    private final String description = "Base Network Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "10s";

    private HardwareAbstractionLayer hardwareAbstractionLayer;
    private List<NetworkIF> interfaces;
    private int refreshCounter = 0;


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isThreaded() {
        return threaded;
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
    public String getInterval() {
        return interval;
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
        if(map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if(map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
    }

    @Override
    public MetricResult getMetrics() {

        ArrayList<Measurement> measurementList = new ArrayList<>();
        if(interfaces == null || refreshCounter++ > 360) {
            log.info("getMetrics() - refreshing list of network interfaces");
            interfaces = hardwareAbstractionLayer.getNetworkIFs();
            refreshCounter = 0;
        }

        for(NetworkIF netif : interfaces) {

            TreeMap<String, String> tagsMap = new TreeMap<String, String>() {{
                put("name", netif.getName());
            }};

            TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
                put("rx_pkts", netif.getPacketsRecv());
                put("tx_pkts", netif.getPacketsSent());
                put("rx_bytes", netif.getBytesRecv());
                put("tx_bytes", netif.getBytesSent());
                put("rx_errs", netif.getInErrors());
                put("tx_errs", netif.getOutErrors());
            }};

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }


        return new MetricResult(name, measurementList);
    }

}
