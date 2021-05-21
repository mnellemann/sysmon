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
public class LinuxDiskExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(LinuxDiskExtension.class);


    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public String getName() {
        return "linux-disk";
    }

    @Override
    public String getProvides() {
        return "disk";
    }

    @Override
    public String getDescription() {
        return "Linux Disk Metrics";
    }


    @Override
    public MetricResult getMetrics() {

        LinuxDiskProcLine proc1 = processFileOutput(readProcFile());
        try {
            Thread.sleep(1 * 1000); // TODO: Configure sample collect time
        } catch (InterruptedException e) {
            log.warn("getMetrics() - sleep interrupted");
            return null;
        }
        LinuxDiskProcLine proc2 = processFileOutput(readProcFile());

        LinuxDiskStat stat = new LinuxDiskStat(proc1, proc2);
        return new MetricResult("disk", new Measurement(stat.getTags(), stat.getFields()));
    }


    protected List<String> readProcFile() {

        List<String> allLines = new ArrayList<>();
        try {
            allLines = Files.readAllLines(Paths.get("/proc/diskstats"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return allLines;
    }


    protected LinuxDiskProcLine processFileOutput(List<String> inputLines) {

        List<String> lines = new ArrayList<>(inputLines.size());
        for(String line : inputLines) {
            String[] splitStr = line.trim().split("\\s+");
            String device = splitStr[2];
            if (device.matches("[sv]d[a-z]{1}") || device.matches("nvme[0-9]n[0-9]")) {
                //log.warn("Going for: " + line);
                lines.add(line);
            }
        }

        return new LinuxDiskProcLine(lines);
    }

}