import org.sysmon.plugins.sysmon_linux.LinuxDiskExtension
import org.sysmon.plugins.sysmon_linux.LinuxDiskProcLine
import org.sysmon.plugins.sysmon_linux.LinuxDiskStat
import spock.lang.Specification

class LinuxDiskTest extends Specification {

    void "test proc file processing"() {

        setup:
        def testFile = new File(getClass().getResource('/diskstats1.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxDiskExtension extension = new LinuxDiskExtension()
        LinuxDiskProcLine procLine = extension.processFileOutput(lines)

        then:
        procLine.getTimeSpentOnIo() == 11145860l
    }


    void "test disk utilization"() {

        setup:
        def testFile1 = new File(getClass().getResource('/diskstats1.txt').toURI())
        def testFile2 = new File(getClass().getResource('/diskstats2.txt').toURI())
        LinuxDiskExtension extension = new LinuxDiskExtension()
        LinuxDiskProcLine procLine1 = extension.processFileOutput(testFile1.readLines())
        LinuxDiskProcLine procLine2 = extension.processFileOutput(testFile2.readLines())

        when:
        LinuxDiskStat diskStat = new LinuxDiskStat(procLine1, procLine2)

        then:
        diskStat.getFields().get("iotime") == 180l
        diskStat.getFields().get("writes") == 108371968l
        diskStat.getFields().get("reads") == 69632l

    }
}
