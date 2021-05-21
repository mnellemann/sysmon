import sysmon.plugins.os_aix.AixMemoryExtension
import sysmon.plugins.os_aix.AixMemoryStat
import spock.lang.Specification

class AixMemoryTest extends Specification {

    void "test AIX svmon output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/svmon.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        AixMemoryExtension extension = new AixMemoryExtension()
        AixMemoryStat stats = extension.processCommandOutput(lines)

        then:
        stats.getFields().get("total") == 4194304L
        stats.getFields().get("used") == 4065060L
        stats.getFields().get("free") == 129244L
        stats.getFields().get("pin") == 1878240L
        stats.getFields().get("virtual") == 2784988L
        stats.getFields().get("available") == 1058012L
        stats.getFields().get("paged") == 524288L
        stats.getFields().get("usage") == 74.775024f

    }

}
