package org.sysmon.plugins.sysmon_linux;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.Measurement;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        LinuxProcessorStat stat = new LinuxProcessorStat(proc2, proc1);

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


