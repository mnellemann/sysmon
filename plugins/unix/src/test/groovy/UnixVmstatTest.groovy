import spock.lang.Specification
import sysmon.plugins.unix.UnixVmstatExtension
import sysmon.plugins.unix.UnixVmstatOutput

class UnixVmstatTest extends Specification {

    void "test AIX vmstat output processing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/vmstat-aix.txt')

        when:
        UnixVmstatExtension extension = new UnixVmstatExtension()
        extension.osType = "aix"
        UnixVmstatOutput stats = extension.processCommandOutput(inputStream)

        then:
        stats.getRunning() == 1
        stats.getBlocked() == 1
    }

    void "test Linux vmstat output processing"() {

        setup:
        InputStream inputStream = getClass().getResourceAsStream('/vmstat-linux.txt')

        when:
        UnixVmstatExtension extension = new UnixVmstatExtension()
        extension.osType = "linux"
        UnixVmstatOutput stats = extension.processCommandOutput(inputStream)

        then:
        stats.getRunning() == 2
        stats.getBlocked() == 1
    }

}
