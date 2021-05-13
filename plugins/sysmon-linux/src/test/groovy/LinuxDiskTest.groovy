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
        procLine.getDevice() == "nvme0n1"
        procLine.getTimeSpentOnIo() == 79560l
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
        diskStat.getTags().get("device") == "nvme0n1"
        diskStat.getFields().get("iotime") == 272l
        diskStat.getFields().get("writes") == 40407040l
        diskStat.getFields().get("reads") == 80896l

    }
}
