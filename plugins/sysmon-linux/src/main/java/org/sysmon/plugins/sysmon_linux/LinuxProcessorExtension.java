package org.sysmon.plugins.sysmon_linux;


import org.pf4j.Extension;
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

    private List<LinuxProcessorStat> currentProcessorStats;
    private List<LinuxProcessorStat> previousProcessorStats;

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public String getGreeting() {
        return "Welcome from Linux ProcessorMetric";
    }

    @Override
    public MetricResult getMetrics() {

        MetricResult result = new MetricResult("processor");
        try {
            copyCurrentValues();
            readProcFile();
            result.setMeasurementList(calculate());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private void readProcFile() throws IOException {

        currentProcessorStats = new ArrayList<>();
        List<String> allLines = Files.readAllLines(Paths.get("/proc/stat"), StandardCharsets.UTF_8);
        for(String line : allLines) {
            if(line.startsWith("cpu")) {
                currentProcessorStats.add(new LinuxProcessorStat(line));
            }
        }

    }


    private void copyCurrentValues() {

        if(currentProcessorStats != null && currentProcessorStats.size() > 0) {
            previousProcessorStats = new ArrayList<>(currentProcessorStats);
        }

    }


    private List<MetricMeasurement> calculate() {

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
}


