package sysmon.plugins.os_linux;

import java.util.HashMap;
import java.util.Map;

public class LinuxNetworkDevStat {

    private final long rxBytes;
    private final long rxPackets;
    private final long txBytes;
    private final long txPackets;


    public LinuxNetworkDevStat(LinuxNetworkDevProcLine previous, LinuxNetworkDevProcLine current) {
        rxBytes = current.getRxBytes() - previous.getRxBytes();
        rxPackets = current.getRxPackets() - previous.getRxPackets();
        txBytes = current.getTxBytes() - previous.getTxBytes();
        txPackets = current.getTxPackets() - previous.getTxPackets();
    }


    public Map<String, String> getTags() {
        return new HashMap<>();
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("rxBytes", rxBytes);
        fields.put("rxPackets", rxPackets);
        fields.put("txBytes", txBytes);
        fields.put("txPackets", txPackets);
        return fields;
    }


}
