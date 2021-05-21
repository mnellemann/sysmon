import sysmon.plugins.os_aix.AixProcessorExtension
import sysmon.plugins.os_aix.AixProcessorStat
import spock.lang.Specification

class AixProcessorTest extends Specification {

    void "test AIX lparstat output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/lparstat-aix.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(lines)

        then:
        stats.getUser() == 83.7f
        stats.getSys() == 3.3f
        stats.getWait() == 0.0f
        stats.getIdle() == 13.0f
        stats.getFields().get("ent") == 0.50f

    }


    void "test Linux lparstat output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/lparstat-linux.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(lines)

        then:
        stats.getUser() == 0.03f
        stats.getSys() == 0.0f
        stats.getWait() == 0.0f
        stats.getIdle() == 99.97f
        stats.getFields().get("ent") == 4.00f
        stats.getFields().get("mode") == "Uncapped"
        stats.getFields().get("type") == "Shared"

    }

}
