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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
public class LinuxMemoryExtension implements MetricExtension {

    private final Pattern pattern = Pattern.compile("^([a-zA-Z]+):\\s+(\\d+)\\s+");

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    @Override
    public String getName() {
        return "linux-memory";
    }

    @Override
    public String getDescription() {
        return "Linux Memory Metrics";
    }


    @Override
    public MetricResult getMetrics() {

        MetricResult result = new MetricResult("memory");
        try {
            result.addMeasurements(readProcFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private List<MeasurementPair> readProcFile() throws IOException {

        List<MeasurementPair> measurementList = new ArrayList<>();

        List<String> allLines = Files.readAllLines(Paths.get("/proc/meminfo"), StandardCharsets.UTF_8);
        for (String line : allLines) {

            if (line.startsWith("Mem")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find() && matcher.groupCount() == 2) {
                    measurementList.add(new MeasurementPair(matcher.group(1), matcher.group(2)));
                }
            }
        }

        return measurementList;
    }

}