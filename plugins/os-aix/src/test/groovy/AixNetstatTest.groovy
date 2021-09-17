import spock.lang.Specification
import sysmon.plugins.os_aix.AixNetstatParser

class AixNetstatTest extends Specification {

    void "test netstat parsing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/netstat-aix.txt')

        when:
        AixNetstatParser parser = new AixNetstatParser(inputStream)

        then:
        parser.getFields().size() > 0
        parser.getFields().get('ip_received') == 76229L
        parser.getFields().get('ip_forwarded') == 24L
        parser.getFields().get('tcp_connections') == 85L
        parser.getFields().get('tcp_pkts_sent') == 31274L
        parser.getFields().get('tcp_pkts_recv') == 39830L
        parser.getFields().get('udp_pkts_sent') == 26332L
        parser.getFields().get('udp_pkts_recv') == 34559L
    }

}
