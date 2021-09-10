package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.*;

@Extension
public class BaseProcessExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseProcessorExtension.class);

    // Extension details
    private final String name = "base_process";
    private final String provides = "process";
    private final String description = "Base Process Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private List<?> includeList = new ArrayList<Object>() {{
        add("java");
    }};

    private SystemInfo systemInfo;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isSupported() {
        systemInfo = BasePlugin.getSystemInfo();
        return systemInfo != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProvides() {
        return provides;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setConfiguration(Map<String, Object> map) {
        if(map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
        if(map.containsKey("include")) {
            includeList = (List<?>) map.get("include");
        }
        log.info(includeList.toString());
    }

    @Override
    public MetricResult getMetrics() {

        ArrayList<Measurement> measurementList = new ArrayList<>();

        List<OSProcess> processList = systemInfo.getOperatingSystem().getProcesses();
        for(OSProcess p : processList) {

            // Skip all the kernel processes
            if(p.getResidentSetSize() < 1) {
                continue;
            }

            String name = p.getName();
            if(!includeList.contains(name)) {
                continue;
            }
            log.debug("pid: " + p.getProcessID() + ", name: " + name + ", virt: " + p.getVirtualSize() + " rss: " + p.getResidentSetSize());

            HashMap<String, String> tagsMap = new HashMap<>();
            HashMap<String, Object> fieldsMap = new HashMap<>();

            tagsMap.put("pid", String.valueOf(p.getProcessID()));
            tagsMap.put("name", name);

            fieldsMap.put("mem_rss", p.getResidentSetSize());
            fieldsMap.put("mem_vsz", p.getVirtualSize());
            fieldsMap.put("kernel_time", p.getKernelTime());
            fieldsMap.put("user_time", p.getUserTime());
            fieldsMap.put("read_bytes", p.getBytesRead());
            fieldsMap.put("write_bytes", p.getBytesWritten());
            fieldsMap.put("files", p.getOpenFiles());
            fieldsMap.put("threads", p.getThreadCount());
            fieldsMap.put("user", p.getUser());
            fieldsMap.put("group", p.getGroup());
            fieldsMap.put("prio", p.getPriority());

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }

        //log.info("Size of measurements: " + measurementList.size());
        return new MetricResult(name, measurementList);
    }

}


