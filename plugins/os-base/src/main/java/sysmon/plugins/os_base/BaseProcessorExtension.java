package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
public class BaseProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseProcessorExtension.class);

    private SystemInfo systemInfo;
    private HardwareAbstractionLayer hardwareAbstractionLayer;

    @Override
    public boolean isSupported() {

        try {
            systemInfo = new SystemInfo();
            hardwareAbstractionLayer = systemInfo.getHardware();
            return true;
        } catch (UnsupportedOperationException e) {
            log.warn(e.getMessage());
        }

        return false;
    }

    @Override
    public String getName() {
        return "base-processor";
    }

    @Override
    public String getProvides() {
        return "processor";
    }

    @Override
    public String getDescription() {
        return "Base Processor Metrics";
    }


    @Override
    public MetricResult getMetrics() {

        Map<String, String> tagsMap = new HashMap<>();
        Map<String, Object> fieldsMap = new HashMap<>();

        long user = 0L;
        long system = 0L;
        long steal = 0L;
        long irq = 0L;
        long softirq = 0L;
        long nice = 0L;
        long idle = 0L;
        long iowait = 0L;

        long[][] ticks = hardwareAbstractionLayer.getProcessor().getProcessorCpuLoadTicks();
        int cores = ticks.length;
        //log.warn("Cores: " + cores);
        for (long[] tick : ticks) {
            nice += tick[CentralProcessor.TickType.NICE.getIndex()];
            user += tick[CentralProcessor.TickType.USER.getIndex()];
            system += tick[CentralProcessor.TickType.SYSTEM.getIndex()];
            steal += tick[CentralProcessor.TickType.STEAL.getIndex()];
            irq += tick[CentralProcessor.TickType.IRQ.getIndex()];
            softirq += tick[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            idle += tick[CentralProcessor.TickType.IDLE.getIndex()];
            iowait += tick[CentralProcessor.TickType.IOWAIT.getIndex()];
        }

        long busy = nice + user + system + steal + irq + softirq;
        long nonBusy = idle + iowait;
        long total = busy + nonBusy;

        /*
        log.info("idle: " + idle);
        log.info("iowait: " + iowait);
        log.info("busy: " + busy);
        log.info("nonBusy: " + nonBusy);
        log.info("total: " + total);
         */

        fieldsMap.put("user", (float) user / (float) total);
        fieldsMap.put("iowait", (float) iowait / (float) total);
        fieldsMap.put("idle", (float) nonBusy / (float) total);
        fieldsMap.put("busy", (float) busy / (float) total);
        fieldsMap.put("system", (float) system / (float) total);

        //log.info(fieldsMap.toString());
        return new MetricResult("processor", new Measurement(tagsMap, fieldsMap));
    }

}


