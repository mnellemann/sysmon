package sysmon.plugins.os_base;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Extension
public class BaseProcessExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(BaseProcessorExtension.class);

    private final List<String> includeList = new ArrayList<String>() {{
        add("java");
        add("influxd");
        add("grafana-server");
    }};

    private SystemInfo systemInfo;

    @Override
    public boolean isSupported() {
        systemInfo = BasePlugin.getSystemInfo();
        return systemInfo != null;
    }

    @Override
    public String getName() {
        return "base_process";
    }

    @Override
    public String getProvides() {
        return "process";
    }

    @Override
    public String getDescription() {
        return "Base Process Metrics";
    }


    @Override
    public MetricResult getMetrics() {

        // TODO: include-list and/or exclude-list of process names

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
            log.info("pid: " + p.getProcessID() + ", name: " + name + ", virt: " + p.getVirtualSize() + " rss: " + p.getResidentSetSize());

            //log.info(p.getProcessID() + " (" + p.getParentProcessID() + ") " + p.getName() + " " + p.getPath());

            HashMap<String, String> tagsMap = new HashMap<>();
            HashMap<String, Object> fieldsMap = new HashMap<>();

            tagsMap.put("pid", String.valueOf(p.getProcessID()));
            tagsMap.put("name", name);

            fieldsMap.put("path", p.getPath());
            fieldsMap.put("mem_rss", p.getResidentSetSize());
            fieldsMap.put("mem_virt", p.getVirtualSize());
            fieldsMap.put("kernel_time", p.getKernelTime());
            fieldsMap.put("user_time", p.getUserTime());
            fieldsMap.put("read_bytes", p.getBytesRead());
            fieldsMap.put("write_bytes", p.getBytesWritten());
            fieldsMap.put("files", p.getOpenFiles());
            fieldsMap.put("threads", p.getThreadCount());
            fieldsMap.put("user", p.getUser());
            fieldsMap.put("group", p.getGroup());

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }

        //log.info("Size of measurements: " + measurementList.size());
        return new MetricResult(getName(), measurementList);
    }

}


