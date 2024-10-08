package sysmon.plugins.base;

import java.util.Map;
import java.util.TreeMap;

import org.pf4j.Extension;

import oshi.SystemInfo;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

@Extension
public class BaseNetstatExtension implements MetricExtension {

    //private static final Logger log = LoggerFactory.getLogger(BaseNetstatExtension.class);

    // Extension details
    private final String name = "base_netstat";
    private final String description = "Base Netstat Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "30s";

    private SystemInfo systemInfo;


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
        systemInfo = BasePlugin.getSystemInfo();
        return systemInfo != null;
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
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if (map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
        if (map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if(map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
    }

    @Override
    public MetricResult getMetrics() {

        TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{

            put("ip_conn_total", systemInfo.getOperatingSystem().getInternetProtocolStats().getConnections().size());

            put("tcp4_conn_active", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv4Stats().getConnectionsActive());
            put("tcp4_conn_passive", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv4Stats().getConnectionsPassive());
            put("tcp4_conn_established", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv4Stats().getConnectionsEstablished());
            put("tcp4_conn_failures", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv4Stats().getConnectionFailures());
            put("tcp4_conn_reset", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv4Stats().getConnectionsReset());

            put("tcp6_conn_active", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv6Stats().getConnectionsActive());
            put("tcp6_conn_passive", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv6Stats().getConnectionsPassive());
            put("tcp6_conn_established", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv6Stats().getConnectionsEstablished());
            put("tcp6_conn_failures", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv6Stats().getConnectionFailures());
            put("tcp6_conn_reset", systemInfo.getOperatingSystem().getInternetProtocolStats().getTCPv6Stats().getConnectionsReset());

            put("udp4_data_sent", systemInfo.getOperatingSystem().getInternetProtocolStats().getUDPv4Stats().getDatagramsSent());
            put("udp4_data_recv", systemInfo.getOperatingSystem().getInternetProtocolStats().getUDPv4Stats().getDatagramsReceived());
            put("udp4_data_recv_error", systemInfo.getOperatingSystem().getInternetProtocolStats().getUDPv4Stats().getDatagramsReceivedErrors());

            put("udp6_data_sent", systemInfo.getOperatingSystem().getInternetProtocolStats().getUDPv6Stats().getDatagramsSent());
            put("udp6_data_recv", systemInfo.getOperatingSystem().getInternetProtocolStats().getUDPv6Stats().getDatagramsReceived());
            put("udp6_data_recv_error", systemInfo.getOperatingSystem().getInternetProtocolStats().getUDPv6Stats().getDatagramsReceivedErrors());

        }};

        return new MetricResult(name, new Measurement(new TreeMap<>(), fieldsMap));
    }

}
