import sysmon.plugins.os_linux.LinuxMemoryExtension
import sysmon.plugins.os_linux.LinuxMemoryStat
import spock.lang.Specification

class LinuxMemoryTest extends Specification {

    void "test Linux free output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/free.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxMemoryExtension extension = new LinuxMemoryExtension()
        LinuxMemoryStat stats = extension.processCommandOutput(lines)

        then:
        stats.getFields().get("total") == 16069172l
        stats.getFields().get("used") == 5896832l
        stats.getFields().get("free") == 4597860l
        stats.getFields().get("shared") == 639780l
        stats.getFields().get("buffers") == 5574480l
        stats.getFields().get("available") == 9192992l
        stats.getFields().get("usage") == 42.79d

    }

}
