import spock.lang.Specification
import sysmon.plugins.os_linux.LinuxDiskExtension
import sysmon.plugins.os_linux.LinuxDiskProcLine
import sysmon.plugins.os_linux.LinuxDiskStat
import sysmon.plugins.os_linux.LinuxNetworkDevProcLine
import sysmon.plugins.os_linux.LinuxNetworkDevStat
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


    void "test /proc/net/dev parsing"() {

        setup:
        def testFile = new File(getClass().getResource('/proc_net_dev1.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxNetworkExtension extension = new LinuxNetworkExtension()
        LinuxNetworkDevProcLine procLine = extension.processDevOutput(lines)

        then:
        procLine.getRxBytes() == 663911036L
        procLine.getRxPackets() == 525522L
        procLine.getRxErrs() == 0L
        procLine.getTxBytes() == 63084294L
        procLine.getTxPackets() == 472869L
        procLine.getTxErrs() == 0L
    }

    void "test dev utilization"() {

        setup:
        def testFile1 = new File(getClass().getResource('/proc_net_dev1.txt').toURI())
        def testFile2 = new File(getClass().getResource('/proc_net_dev2.txt').toURI())
        LinuxNetworkExtension extension = new LinuxNetworkExtension()
        LinuxNetworkDevProcLine procLine1 = extension.processDevOutput(testFile1.readLines())
        LinuxNetworkDevProcLine procLine2 = extension.processDevOutput(testFile2.readLines())

        when:
        LinuxNetworkDevStat networkDevStat = new LinuxNetworkDevStat(procLine1, procLine2)

        then:
        networkDevStat.getFields().get("rxPackets") == 223L
        networkDevStat.getFields().get("rxBytes") == 31501L
        networkDevStat.getFields().get("txBytes") == 46460L
        networkDevStat.getFields().get("txPackets") == 341L

    }

}
