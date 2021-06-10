package sysmon.plugins.os_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LinuxNetstatParser {

    private static final Logger log = LoggerFactory.getLogger(LinuxNetstatParser.class);

    private long ipTotalPacketsReceived;
    private long ipForwarded;
    private long ipIncomingPacketsDiscarded;
    private long ipOutgoingPacketsDropped;

    private long tcpConnectionsEstablished;
    private long tcpSegmentsReceived;
    private long tcpSegmentsSent;

    private long udpPacketsReceived;
    private long udpPacketsSent;


    public LinuxNetstatParser(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
            String line = reader.readLine();
            log.debug("LinuxNetstatParser() - Line: " + line);

            if(line.startsWith("Ip:")) {
                parseIp(reader);
            }

            if(line.startsWith("Tcp:")) {
                parseTcp(reader);
            }

            if(line.startsWith("Udp:")) {
                parseUdp(reader);
            }

        }

        inputStream.close();
    }


    protected void parseIp(BufferedReader reader) throws IOException {

        while (reader.ready()) {
            reader.mark(64);
            String line = reader.readLine();

            if(!line.startsWith(" ")) {
                reader.reset();
                return;
            }

            line = line.trim();

            if(line.matches("(\\d+) total packets received")) {
                ipTotalPacketsReceived = getFirstLong(line);
            }

            if(line.matches("(\\d+) forwarded")) {
                ipForwarded = getFirstLong(line);
            }

            if(line.matches("(\\d+) incoming packets discarded")) {
                ipIncomingPacketsDiscarded = getFirstLong(line);
            }

            if(line.matches("(\\d+) outgoing packets dropped")) {
                ipOutgoingPacketsDropped = getFirstLong(line);
            }

        }

    }


    protected void parseTcp(BufferedReader reader) throws IOException {

        while (reader.ready()) {
            reader.mark(64);
            String line = reader.readLine();

            if(!line.startsWith(" ")) {
                reader.reset();
                return;
            }

            line = line.trim();

            if(line.matches("(\\d+) connections established")) {
                tcpConnectionsEstablished = getFirstLong(line);
            }

            if(line.matches("(\\d+) segments received")) {
                tcpSegmentsReceived = getFirstLong(line);
            }

            if(line.matches("(\\d+) segments sent out")) {
                tcpSegmentsSent = getFirstLong(line);
            }

        }

    }

    protected void parseUdp(BufferedReader reader) throws IOException {

        while (reader.ready()) {
            reader.mark(64);
            String line = reader.readLine();

            if(!line.startsWith(" ")) {
                reader.reset();
                return;
            }

            line = line.trim();

            if(line.matches("(\\d+) packets received")) {
                udpPacketsReceived = getFirstLong(line);
            }

            if(line.matches("(\\d+) packets sent")) {
                udpPacketsSent = getFirstLong(line);
            }
        }

    }


    public Map<String, String> getTags() {
        return new HashMap<>();
    }

    public Map<String, Object> getFields() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("ip_forwarded", ipForwarded);
        fields.put("ip_received", ipTotalPacketsReceived);
        fields.put("ip_dropped", ipOutgoingPacketsDropped);
        fields.put("ip_discarded", ipIncomingPacketsDiscarded);

        fields.put("tcp_connections", tcpConnectionsEstablished);
        fields.put("tcp_pkts_recv", tcpSegmentsReceived);
        fields.put("tcp_pkts_sent", tcpSegmentsSent);

        fields.put("udp_pkts_recv", udpPacketsReceived);
        fields.put("udp_pkts_sent", udpPacketsSent);

        return fields;
    }

    private Long getFirstLong(String line) {
        return Long.parseLong(line.substring(0, line.indexOf(" ")));
    }

}
