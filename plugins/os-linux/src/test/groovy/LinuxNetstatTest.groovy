import spock.lang.Specification
import sysmon.plugins.os_linux.LinuxNetstatParser

class LinuxNetstatTest extends Specification {

    void "test netstat parsing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/netstat-linux.txt');

        when:
        LinuxNetstatParser parser = new LinuxNetstatParser(inputStream)

        then:
        parser.getFields().size() > 0
        parser.getFields().get('ip_received') == 109772L
        parser.getFields().get('ip_dropped') == 70L
        parser.getFields().get('ip_discarded') == 0L
        parser.getFields().get('tcp_pkts_sent') == 89891L
        parser.getFields().get('tcp_pkts_recv') == 86167L
        parser.getFields().get('udp_pkts_sent') == 10682L
        parser.getFields().get('udp_pkts_recv') == 31928L

    }

}
