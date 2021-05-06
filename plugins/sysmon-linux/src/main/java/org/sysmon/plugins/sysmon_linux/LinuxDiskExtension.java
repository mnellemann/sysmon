package org.sysmon.plugins.sysmon_linux;

import org.pf4j.Extension;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MeasurementPair;
import org.sysmon.shared.MetricResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Extension
public class LinuxDiskExtension implements MetricExtension {

    private final static List<String> ignoreList = new ArrayList<String>() {{
        add("dm-");
        add("loop");
    }};

    private List<LinuxDiskStat> currentDiskStats;
    private List<LinuxDiskStat> previousDiskStats;

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public String getName() {
        return "linux-disk";
    }

    @Override
    public String getDescription() {
        return "Linux Disk Metrics";
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


    private List<MeasurementPair> calculate() {

        List<MeasurementPair> measurementList = new ArrayList<>();

        if(previousDiskStats == null || previousDiskStats.size() != currentDiskStats.size()) {
            return measurementList;
        }


        for(int i = 0; i < currentDiskStats.size(); i++) {

            LinuxDiskStat curStat = currentDiskStats.get(i);
            LinuxDiskStat preStat = previousDiskStats.get(i);

            AtomicBoolean ignore = new AtomicBoolean(false);
            ignoreList.forEach(str -> {
                if(curStat.getDevice().startsWith(str)) {
                    ignore.set(true);
                }
            });


            if(!ignore.get()) {
                long timeSpendDoingIo = curStat.getTimeSpentOnIo() - preStat.getTimeSpentOnIo();
                // TODO: Calculate differences for wanted disk io stats
                measurementList.add(new MeasurementPair(curStat.getDevice() + "-iotime", timeSpendDoingIo));
            }

        }

        return measurementList;
    }

}
