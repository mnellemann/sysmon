package sysmon.plugins.os_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AixNetstatParser {

    private static final Logger log = LoggerFactory.getLogger(AixNetstatParser.class);

    private long ipTotalPacketsReceived;
    private long ipForwarded;

    private long tcpConnectionsEstablished;
    private long tcpPacketsReceved;
    private long tcpPacketsSent;

    private long udpPacketsReceived;
    private long udpPacketsSent;


    public AixNetstatParser(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
            String line = reader.readLine();
            log.debug("AixNetstatParser() - Line: " + line);

            if(line.startsWith("tcp:")) {
                parseTcp(reader);
            }

            if(line.startsWith("udp:")) {
                parseUdp(reader);
            }

            if(line.startsWith("ip:")) {
                parseIp(reader);
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

            if(line.matches("(\\d+) packets forwarded")) {
                ipForwarded = getFirstLong(line);
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

            if(line.matches("(\\d+) connections established \\(including accepts\\)")) {
                tcpConnectionsEstablished = getFirstLong(line);
            }

            if(line.matches("(\\d+) packets received")) {
                tcpPacketsReceved = getFirstLong(line);
            }

            if(line.matches("(\\d+) packets sent")) {
                tcpPacketsSent = getFirstLong(line);
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

            if(line.matches("(\\d+) datagrams received")) {
                udpPacketsReceived = getFirstLong(line);
            }

            if(line.matches("(\\d+) datagrams output")) {
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

        fields.put("tcp_connections", tcpConnectionsEstablished);
        fields.put("tcp_pkts_recv", tcpPacketsReceved);
        fields.put("tcp_pkts_sent", tcpPacketsSent);

        fields.put("udp_pkts_recv", udpPacketsReceived);
        fields.put("udp_pkts_sent", udpPacketsSent);

        return fields;
    }

    private Long getFirstLong(String line) {
        return Long.parseLong(line.substring(0, line.indexOf(" ")));
    }

}
