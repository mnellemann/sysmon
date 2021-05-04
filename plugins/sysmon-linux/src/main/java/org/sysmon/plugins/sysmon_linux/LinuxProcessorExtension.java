package org.sysmon.plugins.sysmon_linux;


import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricMeasurement;
import org.sysmon.shared.MetricResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Extension
public class LinuxProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(LinuxProcessorExtension.class);

    private List<LinuxProcessorStat> currentProcessorStats;
    private List<LinuxProcessorStat> previousProcessorStats;


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

        if(currentProcessorStats != null && currentProcessorStats.size() > 0) {
            previousProcessorStats = new ArrayList<>(currentProcessorStats);
        }

        MetricResult result = new MetricResult("processor");
        currentProcessorStats = processFileOutput(readProcFile());
        result.setMetricMeasurementList(calculateDifference());

        return result;
    }


    private List<MetricMeasurement> calculateDifference() {

        List<MetricMeasurement> measurementList = new ArrayList<>();

        if(previousProcessorStats == null || previousProcessorStats.size() != currentProcessorStats.size()) {
            return measurementList;
        }

        for(int i = 0; i < currentProcessorStats.size(); i++) {

            LinuxProcessorStat curStat = currentProcessorStats.get(i);
            LinuxProcessorStat preStat = previousProcessorStats.get(i);

            long workTimeDiff = curStat.getCombinedTime() - preStat.getCombinedTime();
            long idleTimeDiff = curStat.getCombinedIdleTime() - preStat.getCombinedIdleTime();
            float percentUsage = (float) (workTimeDiff - idleTimeDiff) / workTimeDiff;

            Integer pct = (int) (percentUsage * 100);
            measurementList.add(new MetricMeasurement(curStat.getCpuName(), pct));

        }

        return  measurementList;

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


    protected List<LinuxProcessorStat> processFileOutput(List<String> inputLines) {

        List<LinuxProcessorStat> processorStats = new ArrayList<>();
        for(String line : inputLines) {
            if(line.matches("^cpu\\d+.*")) {
                processorStats.add(new LinuxProcessorStat(line));
            }
        }

        return processorStats;
    }


}


