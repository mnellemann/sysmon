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
        stats.getFields().get("total") == 4194304l
        stats.getFields().get("used") == 4065060l
        stats.getFields().get("free") == 129244l
        stats.getFields().get("pin") == 1878240l
        stats.getFields().get("virtual") == 2784988l
        stats.getFields().get("available") == 1058012l
        stats.getFields().get("paged") == 524288l
        stats.getFields().get("usage") == 74.78d

    }

}
