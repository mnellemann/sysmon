import org.sysmon.plugins.sysmon_aix.AixProcessorExtension
import org.sysmon.plugins.sysmon_aix.AixProcessorStat
import spock.lang.Specification

class AixProcessorTest extends Specification {

    void "test lparstat output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/lparstat.txt').toURI())
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


}
