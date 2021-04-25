package org.sysmon.agent.beans;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.spi.Configurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricBean;
import org.sysmon.shared.MetricMeasurement;
import org.sysmon.shared.MetricResult;

@Configurer
public class MemoryBean implements MetricBean {

    private final static Logger log = LoggerFactory.getLogger(MemoryBean.class);
    private final Pattern pattern = Pattern.compile("^([a-zA-Z]+):\\s+(\\d+)\\s+");


    @Override
    public MetricResult getMetrics() {
        
        MetricResult result = new MetricResult("memory");
        try {
            result.setMeasurementList(readProcFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



    private List<MetricMeasurement> readProcFile() throws IOException {

        List<MetricMeasurement> measurementList = new ArrayList<>();

        List<String> allLines = Files.readAllLines(Paths.get("/proc/meminfo"), StandardCharsets.UTF_8);
        for(String line : allLines) {

            if(line.startsWith("Mem")) {
                Matcher matcher = pattern.matcher(line);
                if(matcher.find() && matcher.groupCount() == 2) {
                    measurementList.add(new MetricMeasurement(matcher.group(1), matcher.group(2)));
                }
            }
        }

        return measurementList;
    }

}