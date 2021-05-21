import sysmon.plugins.os_aix.AixDiskExtension
import sysmon.plugins.os_aix.AixDiskStat
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
        //stats.getTags().get("device") == "hdisk0"
        stats.getFields().get("reads") == 757760L
        stats.getFields().get("writes") == 12288L
        stats.getFields().get("kbps") == 752.0F
        stats.getFields().get("tps") == 81.0F

    }

}
