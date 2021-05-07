import org.sysmon.plugins.sysmon_linux.LinuxProcessorExtension
import org.sysmon.plugins.sysmon_linux.LinuxProcessorProcLine
import org.sysmon.plugins.sysmon_linux.LinuxProcessorStat
import spock.lang.Specification

class LinuxProcessorTest extends Specification {

    void "test proc file processing"() {

        setup:
        def testFile = new File(getClass().getResource('/proc1.txt').toURI())
        List<String> lines = testFile.readLines("UTF-8")

        when:
        LinuxProcessorExtension extension = new LinuxProcessorExtension()
        List<LinuxProcessorProcLine> procLines = extension.processFileOutput(lines)

        then:
        procLines[0].getSystemTime() == 4686l
        procLines[0].getUserTime() == 27477l
        procLines[0].getIdleTime() == 281276l
        procLines[0].getIoWaitTime() == 252l

    }


    void "test processor utlization"() {

        setup:
        def testFile1 = new File(getClass().getResource('/proc1.txt').toURI())
        def testFile2 = new File(getClass().getResource('/proc2.txt').toURI())
        LinuxProcessorProcLine processorProcLine1 = new LinuxProcessorProcLine(testFile1.readLines().get(1))
        LinuxProcessorProcLine processorProcLine2 = new LinuxProcessorProcLine(testFile2.readLines().get(1))

        when:
        LinuxProcessorStat processorStat = new LinuxProcessorStat(processorProcLine1, processorProcLine2)

        then:
        processorStat.getMeasurements().getName() == "cpu0"
        processorStat.getMeasurements().getValue() == 42.13362f

    }


}
