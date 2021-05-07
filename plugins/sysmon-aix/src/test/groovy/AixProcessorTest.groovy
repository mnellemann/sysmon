import org.sysmon.plugins.sysmon_aix.AixProcessorExtension
import org.sysmon.plugins.sysmon_aix.AixProcessorStat
import spock.lang.Specification

class AixProcessorTest extends Specification {

    void "test mpstat output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/mpstat1.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        AixProcessorExtension extension = new AixProcessorExtension()
        List<AixProcessorStat> stats = extension.processCommandOutput(lines)

        then:
        stats[0].getCombinedWorkTime() == 85.1f
        stats[0].getCombinedTime() == 100.0f
        stats[0].getSystemTime() == 18.4f
        stats[0].getUserTime() == 66.7f
        stats[0].getWaitTime() == 0.0f
        stats[0].getIdleTime() == 14.9f

    }

}
