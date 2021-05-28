import sysmon.plugins.os_linux.LinuxDiskExtension
import sysmon.plugins.os_linux.LinuxDiskProcLine
import sysmon.plugins.os_linux.LinuxDiskStat
import spock.lang.Specification

class LinuxDiskTest extends Specification {

    void "test proc file processing"() {

        setup:
        def testFile = new File(getClass().getResource('/proc_diskstats1.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxDiskExtension extension = new LinuxDiskExtension()
        LinuxDiskProcLine procLine = extension.processFileOutput(lines)

        then:
        procLine.getTimeSpentOnIo() == 11145860l
    }


    void "test disk utilization"() {

        setup:
        def testFile1 = new File(getClass().getResource('/proc_diskstats1.txt').toURI())
        def testFile2 = new File(getClass().getResource('/proc_diskstats2.txt').toURI())
        LinuxDiskExtension extension = new LinuxDiskExtension()
        LinuxDiskProcLine procLine1 = extension.processFileOutput(testFile1.readLines())
        LinuxDiskProcLine procLine2 = extension.processFileOutput(testFile2.readLines())

        when:
        LinuxDiskStat diskStat = new LinuxDiskStat(procLine1, procLine2)

        then:
        diskStat.getFields().get("iotime") == 180L
        diskStat.getFields().get("writes") == 108371968L
        diskStat.getFields().get("reads") == 69632L
        diskStat.getFields().get("kbps") == 105900.0f
        diskStat.getFields().get("tps") == 97.0f

    }
}
