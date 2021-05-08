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

        if(currentProcessorProc != null && currentProcessorProc.size() > 0) {
            previousProcessorProc = new ArrayList<>(currentProcessorProc);
        }
        currentProcessorProc = processFileOutput(readProcFile());

        MetricResult result = new MetricResult("processor");
        if(previousProcessorProc == null || previousProcessorProc.size() != currentProcessorProc.size()) {
            return result;
        }


        List<Measurement> measurementList = new ArrayList<>();
        for(int i = 0; i < currentProcessorProc.size(); i++) {
            LinuxProcessorStat stat = new LinuxProcessorStat(currentProcessorProc.get(i), previousProcessorProc.get(i));

            Map<String, String> tagsMap = new HashMap<>();
            tagsMap.put("cpu", stat.getName());

            Map<String, Object> fieldsMap = stat.getFields();

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }

        result.addMeasurements(measurementList);
        return result;
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


    protected List<LinuxProcessorProcLine> processFileOutput(List<String> inputLines) {

        List<LinuxProcessorProcLine> processorStats = new ArrayList<>();
        for(String line : inputLines) {
            if(line.matches("^cpu\\d+.*")) {
                processorStats.add(new LinuxProcessorProcLine(line));
            }
        }

        return processorStats;
    }


}


