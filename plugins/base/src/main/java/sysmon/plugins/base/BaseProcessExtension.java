package sysmon.plugins.base;

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
    private final String description = "Base Process Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
                private String interval = "60s";

    private List<?> includeList = new ArrayList<Object>() {{
        add("java");
        add("node");
        add("sshd");
        add("httpd");
        add("nginx");
        add("mongod");
        add("mysqld");
        add("apache2");
        add("influxd");
        add("haproxy");
        add("beam.smp");
        add("filebeat");
        add("corosync");
        add("rsyslogd");
        add("postgres");
        add("mariadbd");
        add("memcached");
        add("db2sysc");
        add("dsmserv");
        add("mmfsd");
        add("systemd");
        add("php-fpm");
        add("clamd");
        add("freshclam");
        add("dovecot");
        add("grafana");
    }};

    private static final long MINIMUM_UPTIME_SECONDS = 600;
    private SystemInfo systemInfo;


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
        systemInfo = BasePlugin.getSystemInfo();
        return systemInfo != null;
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
        if(map.containsKey("enabled")) {
            enabled = (boolean) map.get("enabled");
        }
        if(map.containsKey("threaded")) {
            threaded = (boolean) map.get("threaded");
        }
        if(map.containsKey("interval")) {
            interval = (String) map.get("interval");
        }
        if(map.containsKey("include")) {
            includeList = (List<?>) map.get("include");
        }
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

            // Skip short-lived processes
            if(p.getUpTime() < (MINIMUM_UPTIME_SECONDS * 1000)) {
                continue;
            }

            // Skip process names not found in our includeList, only if the list is not empty or null
            if(includeList != null && !includeList.isEmpty() && !includeList.contains(p.getName())) {
                continue;
            }
            log.debug("pid: " + p.getProcessID() + ", name: " + p.getName() + ", virt: " + p.getVirtualSize() + " rss: " + p.getResidentSetSize());

            TreeMap<String, String> tagsMap = new TreeMap<String, String>() {{
                put("pid", String.valueOf(p.getProcessID()));
                put("name", p.getName());
            }};

            TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
                put("mem_rss", p.getResidentSetSize());
                put("mem_vsz", p.getVirtualSize());
                put("kernel_time", p.getKernelTime());
                put("user_time", p.getUserTime());
                put("read_bytes", p.getBytesRead());
                put("write_bytes", p.getBytesWritten());
                put("files", p.getOpenFiles());
                put("threads", p.getThreadCount());
                put("user", p.getUser());
                put("group", p.getGroup());
                put("prio", p.getPriority());
            }};

            measurementList.add(new Measurement(tagsMap, fieldsMap));
        }

        //log.info("Size of measurements: " + measurementList.size());
        return new MetricResult(name, measurementList);
    }

}


