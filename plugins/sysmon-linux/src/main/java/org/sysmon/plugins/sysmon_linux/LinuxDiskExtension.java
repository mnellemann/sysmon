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
public class LinuxDiskExtension implements MetricExtension {

    private List<LinuxDiskStat> currentDiskStats;
    private List<LinuxDiskStat> previousDiskStats;

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public String getGreeting() {
        return "Welcome from Linux DiskMetric";
    }

    @Override
    public MetricResult getMetrics() {

        MetricResult result = new MetricResult("disk");
        try {
            copyCurrentValues();
            readProcFile();
            result.setMetricMeasurementList(calculate());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



    private void readProcFile() throws IOException {

        currentDiskStats = new ArrayList<>();
        List<String> allLines = Files.readAllLines(Paths.get("/proc/diskstats"), StandardCharsets.UTF_8);
        for(String line : allLines) {
            currentDiskStats.add(new LinuxDiskStat(line));
        }

    }


    private void copyCurrentValues() {

        if(currentDiskStats != null && currentDiskStats.size() > 0) {
            previousDiskStats = new ArrayList<>(currentDiskStats);
        }

    }


    private List<MetricMeasurement> calculate() {

        List<MetricMeasurement> measurementList = new ArrayList<>();

        if(previousDiskStats == null || previousDiskStats.size() != currentDiskStats.size()) {
            return measurementList;
        }


        for(int i = 0; i < currentDiskStats.size(); i++) {

            LinuxDiskStat curStat = currentDiskStats.get(i);
            LinuxDiskStat preStat = previousDiskStats.get(i);

            if(curStat.getDevice().startsWith("loop")) {
                continue;
            }

            long timeSpendDoingIo = curStat.getTimeSpentOnIo() - preStat.getTimeSpentOnIo();

            // TODO: Calculate differences for wanted disk io stats
            measurementList.add(new MetricMeasurement(curStat.getDevice() + "-iotime", timeSpendDoingIo));

        }

        return measurementList;
    }

}
