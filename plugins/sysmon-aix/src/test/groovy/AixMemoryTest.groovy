import org.sysmon.plugins.sysmon_aix.AixMemoryExtension
import org.sysmon.plugins.sysmon_aix.AixMemoryStat
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
        stats.getFields().get("used") == 4036532l
        stats.getFields().get("free") == 157772l
        stats.getFields().get("virtual") == 2335076l
        stats.getFields().get("available") == 1652640l
        stats.getFields().get("usage") == 60.6d
        stats.getTags().get("mode") == "Ded"

    }

}
