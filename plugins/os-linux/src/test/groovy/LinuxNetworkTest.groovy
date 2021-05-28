import spock.lang.Specification
import sysmon.plugins.os_linux.LinuxNetworkExtension
import sysmon.plugins.os_linux.LinuxNetworkSockStat

class LinuxNetworkTest extends Specification {

    void "test /proc/net/sockstat parsing"() {

        setup:
        def testFile = new File(getClass().getResource('/proc_net_sockstat.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxNetworkExtension extension = new LinuxNetworkExtension()
        LinuxNetworkSockStat stats = extension.processSockOutput(lines)

        then:
        stats.getFields().get("sockets") == 1238L
        stats.getFields().get("tcp_inuse") == 52L
        stats.getFields().get("tcp_orphan") == 0L
        stats.getFields().get("tcp_alloc") == 55L
        stats.getFields().get("tcp_mem") == 7l
        stats.getFields().get("tcp_tw") == 18L
        stats.getFields().get("udp_inuse") == 11L
        stats.getFields().get("udp_mem") == 10L

    }

}
