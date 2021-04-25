package org.sysmon.agent.beans;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.spi.Configurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sysmon.shared.MetricBean;
import org.sysmon.shared.MetricMeasurement;
import org.sysmon.shared.MetricResult;

@Configurer
public class ProcessorBean implements MetricBean {

    private final static Logger log = LoggerFactory.getLogger(ProcessorBean.class);

    private List<LinuxProcessorStat> currentProcessorStats;
    private List<LinuxProcessorStat> previousProcessorStats;


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
                log.debug(line);
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


    public static class LinuxProcessorStat {

        private final String cpuName;
        private final Long userTime;
        private final Long niceTime;
        private final Long systemTime;
        private final Long idleTime;
        private final Long ioWaitTime;
        private final Long irqTime;
        private final Long softIrqTime;
        private final Long stealTime;
        private final Long guestTime;
        private final Long guestNiceTime;


        LinuxProcessorStat(String procString) {

            String[] splitStr = procString.trim().split("\\s+");
            if(splitStr.length != 11) {
                throw new UnsupportedOperationException("Linux proc CPU string error: " + procString);
            }

            this.cpuName = splitStr[0];
            this.userTime = Long.parseLong(splitStr[1]);
            this.niceTime = Long.parseLong(splitStr[2]);
            this.systemTime = Long.parseLong(splitStr[3]);
            this.idleTime = Long.parseLong(splitStr[4]);
            this.ioWaitTime = Long.parseLong(splitStr[5]);
            this.irqTime = Long.parseLong(splitStr[6]);
            this.softIrqTime = Long.parseLong(splitStr[7]);
            this.stealTime = Long.parseLong(splitStr[8]);
            this.guestTime = Long.parseLong(splitStr[9]);
            this.guestNiceTime = Long.parseLong(splitStr[10]);

        }

        public String getCpuName() {
            return cpuName;
        }

        public Long getUserTime() {
            return userTime;
        }

        public Long getNiceTime() {
            return niceTime;
        }

        public Long getSystemTime() {
            return systemTime;
        }

        public Long getIdleTime() {
            return idleTime;
        }

        public Long getIoWaitTime() {
            return ioWaitTime;
        }

        public Long getIrqTime() {
            return irqTime;
        }

        public Long getSoftIrqTime() {
            return softIrqTime;
        }

        public Long getStealTime() {
            return stealTime;
        }

        public Long getGuestTime() {
            return guestTime;
        }

        public Long getGuestNiceTime() {
            return guestNiceTime;
        }

        public Long getCombinedIdleTime() {
            return idleTime + ioWaitTime;
        }

        public Long getCombinedWorkTime() {
            return userTime + niceTime + systemTime + irqTime + softIrqTime + stealTime + guestTime + guestNiceTime;
        }

        public Long getCombinedTime() {
            return getCombinedIdleTime() + getCombinedWorkTime();
        }

    }

}


