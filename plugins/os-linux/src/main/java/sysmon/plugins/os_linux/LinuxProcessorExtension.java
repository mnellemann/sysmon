package sysmon.plugins.os_linux;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Extension
public class LinuxProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(LinuxProcessorExtension.class);

    private List<LinuxProcessorProcLine> currentProcessorProc;
    private List<LinuxProcessorProcLine> previousProcessorProc;


    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public String getName() {
        return "linux-processor";
    }

    @Override
    public String getProvides() {
        return "processor";
    }

    @Override
    public String getDescription() {
        return "Linux Processor Metrics";
    }


    @Override
    public MetricResult getMetrics() {

        LinuxProcessorProcLine proc1 = processFileOutput(readProcFile());

        try {
            Thread.sleep(1 * 1000); // TODO: Configure sample collect time
        } catch (InterruptedException e) {
            log.warn("getMetrics() - sleep interrupted");
            return null;
        }

        LinuxProcessorProcLine proc2 = processFileOutput(readProcFile());

        LinuxProcessorStat stat = new LinuxProcessorStat(proc1, proc2);

        return new MetricResult("processor", new Measurement(stat.getTags(), stat.getFields()));
    }



    protected List<String> readProcFile() {

        List<String> allLines = new ArrayList<>();
        try {
            allLines = Files.readAllLines(Paths.get("/proc/stat"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return allLines;

    }


    protected LinuxProcessorProcLine processFileOutput(List<String> inputLines) {

        for(String line : inputLines) {
            if(line.matches("^cpu\\S+.*")) {
                return new LinuxProcessorProcLine(line);
            }
        }

        return null;
    }


}


