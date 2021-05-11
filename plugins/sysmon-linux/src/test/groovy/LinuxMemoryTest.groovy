import org.sysmon.plugins.sysmon_linux.LinuxMemoryExtension
import org.sysmon.shared.Measurement
import spock.lang.Specification

class LinuxMemoryTest extends Specification {

    void "test proc file processing"() {

        setup:
        def testFile = new File(getClass().getResource('/meminfo.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxMemoryExtension extension = new LinuxMemoryExtension()
        Measurement m = extension.processProcFile(lines);

        then:
        m.getFields().get("total") == 16069616
        m.getFields().get("available") == 7968744
        m.getFields().get("usage") == 50.41
    }

}
