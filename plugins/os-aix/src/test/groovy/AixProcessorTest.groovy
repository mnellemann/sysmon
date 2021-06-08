import sysmon.plugins.os_aix.AixProcessorExtension
import sysmon.plugins.os_aix.AixProcessorStat
import spock.lang.Specification

class AixProcessorTest extends Specification {

    void "test AIX lparstat shared output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/lparstat-aix-shared.txt').toURI())
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
        stats.getFields().get("type") == "Shared"

    }

    void "test AIX lparstat dedicated output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/lparstat-aix-dedicated.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        AixProcessorStat stats = extension.processCommandOutput(lines)

        then:
        stats.getUser() == 0.1f
        stats.getSys() == 0.2f
        stats.getWait() == 0.0f
        stats.getIdle() == 99.7f
        stats.getFields().get("physc") == 0.07f
        stats.getFields().get("type") == "Dedicated"

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

    // java.lang.UnsupportedOperationException: lparstat string error:   2.2   1.2    0.0   96.6  0.28  1100   132  24.23

}
