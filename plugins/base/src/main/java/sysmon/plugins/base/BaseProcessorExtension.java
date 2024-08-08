package sysmon.plugins.base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;
import sysmon.shared.PluginHelper;

import java.util.Map;
import java.util.TreeMap;

@Extension
public class BaseProcessorExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseProcessorExtension.class);

    // Extension details
    private final String name = "base_processor";
    private final String description = "Base Processor Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "30s";

    private HardwareAbstractionLayer hardwareAbstractionLayer;
    private long[] oldTicks;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isThreaded() {
        return threaded;
    }

    @Override
    public boolean isSupported() {
        hardwareAbstractionLayer = BasePlugin.getHardwareAbstractionLayer();
        return hardwareAbstractionLayer != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInterval() {
        return interval;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if (map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
        if(map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if(map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
    }

    @Override
    public MetricResult getMetrics() {

        TreeMap<String, String> tagsMap = new TreeMap<>();
        TreeMap<String, Object> fieldsMap = new TreeMap<>();

        long[] ticks = hardwareAbstractionLayer.getProcessor().getSystemCpuLoadTicks();
        if(oldTicks == null || oldTicks.length != ticks.length) {
            oldTicks = ticks;
            return null;
        }

        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - oldTicks[CentralProcessor.TickType.NICE.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - oldTicks[CentralProcessor.TickType.USER.getIndex()];
        long system = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - oldTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - oldTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - oldTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - oldTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];

        long busy = nice + user + system + steal + irq + softirq;
        long nonBusy = idle + iowait;
        long total = busy + nonBusy;

        fieldsMap.put("system", PluginHelper.round(((double) system / (double) total) * 100, 2));
        fieldsMap.put("user", PluginHelper.round(((double) user / (double) total) * 100, 2));
        fieldsMap.put("nice", PluginHelper.round(((double) nice / (double) total) * 100, 2));
        fieldsMap.put("iowait", PluginHelper.round(((double) iowait / (double) total) * 100, 2));
        fieldsMap.put("steal", PluginHelper.round(((double) steal / (double) total) * 100, 2));
        fieldsMap.put("irq", PluginHelper.round(((double) irq / (double) total) * 100, 2));
        fieldsMap.put("softirq", PluginHelper.round(((double) softirq / (double) total) * 100, 2));
        fieldsMap.put("idle", PluginHelper.round(((double) idle / (double) total) * 100, 2));
        fieldsMap.put("busy", PluginHelper.round(((double) busy / (double) total) * 100, 2));

        oldTicks = ticks;
        log.debug(fieldsMap.toString());
        return new MetricResult(name, new Measurement(tagsMap, fieldsMap));
    }

}


