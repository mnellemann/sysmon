import org.sysmon.plugins.sysmon_aix.AixDiskExtension
import org.sysmon.plugins.sysmon_aix.AixDiskStat
import spock.lang.Specification

class AixDiskTest extends Specification {

    void "test AIX iostat output processing"() {

        setup:
        def testFile = new File(getClass().getResource('/iostat.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        AixDiskExtension extension = new AixDiskExtension()
        AixDiskStat stats = extension.processCommandOutput(lines)

        then:
        stats.getTags().get("device") == "hdisk0"
        stats.getFields().get("reads") == 757760l
        stats.getFields().get("writes") == 12288l

    }

}
