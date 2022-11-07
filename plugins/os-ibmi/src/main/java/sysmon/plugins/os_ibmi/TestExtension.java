package sysmon.plugins.os_ibmi;

import com.ibm.as400.access.*;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.Measurement;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

//@Extension
public class TestExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(TestExtension.class);

    // Extension details
    private final String name = "ibmi_test";
    private final String provides = "ibmi_test";
    private final String description = "IBM i Test Metrics";

    // Configuration / Options
    private boolean enabled = true;
    private boolean threaded = false;
    private String interval = "10s";

    private SystemStatus systemStatus;


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
        systemStatus = IbmIPlugin.getSystemStatus();
        return systemStatus != null;
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
    public String getProvides() {
        return provides;
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

        if(systemStatus == null) {
            log.warn("getMetrics() - no system or status");
            return null;
        }

        /* const hSql = "select
        SERVER_NAME,
        HTTP_FUNCTION,
        SERVER_NORMAL_CONNECTIONS,
        SERVER_ACTIVE_THREADS,
        SERVER_IDLE_THREADS,
        BYTES_RECEIVED,
        BYTES_SENT,
        NONCACHE_PROCESSING_TIME,
        CACHE_PROCESSING_TIME
         from
         QSYS2.HTTP_SERVER_INFO";
         */

        try {
            int jobsInSystem = systemStatus.getJobsInSystem();
            log.info("Jobs In System: {}", jobsInSystem);

            int batchJobsRunning = systemStatus.getBatchJobsRunning();
            log.info("Batch Jobs Running: {}", batchJobsRunning);

            int activeThreads = systemStatus.getActiveThreadsInSystem();
            log.info("Active Threads: {}", activeThreads);

            int activeJobs = systemStatus.getActiveJobsInSystem();
            log.info("Active Jobs: {}", activeJobs);

            int onlineUsers = systemStatus.getUsersCurrentSignedOn();
            log.info("Online Users: {}", onlineUsers);

            // The storage capacity of the system auxiliary storage pool (ASP1) in MBytes.
            long systemAsp = systemStatus.getSystemASP();

            System.out.println("Current Processing Capacity :" + systemStatus.getCurrentProcessingCapacity());
            System.out.println("ASPUsed:" + systemStatus.getPercentSystemASPUsed());
            System.out.println("Temp Addresses Used:" + systemStatus.getPercentTemporaryAddresses());


            TreeMap<String, Object> fieldsMap = new TreeMap<String, Object>() {{
                put("jobs_total", jobsInSystem);
                put("jobs_running", batchJobsRunning);
                put("jobs_active", activeJobs);
                put("threads", activeThreads);
                put("users", onlineUsers);

            }};
            return new MetricResult(name, new Measurement(new TreeMap<>(), fieldsMap));

        } catch (AS400SecurityException | ErrorCompletingRequestException | InterruptedException | IOException | ObjectDoesNotExistException e) {
            log.error("getMetrics() {}", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
