package sysmon.plugins.os_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxNetworkSockStat {

    private static final Logger log = LoggerFactory.getLogger(LinuxNetworkSockStat.class);

    private static final Pattern pattern1 = Pattern.compile("^sockets: used (\\d+)");
    private static final Pattern pattern2 = Pattern.compile("^TCP: inuse (\\d+) orphan (\\d+) tw (\\d+) alloc (\\d+) mem (\\d+)");
    private static final Pattern pattern3 = Pattern.compile("^UDP: inuse (\\d+) mem (\\d+)");

    private long sockets;
    private long tcp_inuse;
    private long tcp_orphan;
    private long tcp_tw;
    private long tcp_alloc;
    private long tcp_mem;
    private long udp_inuse;
    private long udp_mem;

    /*
    sockets: used 1238
    TCP: inuse 52 orphan 0 tw 18 alloc 55 mem 7
    UDP: inuse 11 mem 10
    UDPLITE: inuse 0
    RAW: inuse 0
    FRAG: inuse 0 memory 0
    */


    LinuxNetworkSockStat(List<String> lines) {

        Matcher matcher;
        for(String line : lines) {
            String proto = line.substring(0, line.indexOf(':'));

            switch (proto) {
                case "sockets":
                    matcher = pattern1.matcher(line);
                    if (matcher.matches() && matcher.groupCount() == 1) {
                        sockets = Long.parseLong(matcher.group(1));
                    }
                    break;

                case "TCP":
                    matcher = pattern2.matcher(line);
                    if (matcher.matches() && matcher.groupCount() == 5) {
                        tcp_inuse = Long.parseLong(matcher.group(1));
                        tcp_orphan = Long.parseLong(matcher.group(2));
                        tcp_tw = Long.parseLong(matcher.group(3));
                        tcp_alloc = Long.parseLong(matcher.group(4));
                        tcp_mem = Long.parseLong(matcher.group(5));
                    }
                    break;

                case "UDP":
                    matcher = pattern3.matcher(line);
                    if (matcher.matches() && matcher.groupCount() == 2) {
                        udp_inuse = Long.parseLong(matcher.group(1));
                        udp_mem = Long.parseLong(matcher.group(2));
                    }
                    break;

            }

        }

    }


    public Map<String, String> getTags() {
        return new HashMap<>();
    }


    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("sockets", sockets);
        fields.put("tcp_inuse", tcp_inuse);
        fields.put("tcp_alloc", tcp_alloc);
        fields.put("tcp_orphan", tcp_orphan);
        fields.put("tcp_mem", tcp_mem);
        fields.put("tcp_tw", tcp_tw);
        fields.put("udp_inuse", udp_inuse);
        fields.put("udp_mem", udp_mem);
        return fields;
    }

}
