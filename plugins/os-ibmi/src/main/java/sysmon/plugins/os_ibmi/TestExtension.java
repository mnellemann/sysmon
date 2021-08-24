package sysmon.plugins.os_ibmi;

import com.ibm.as400.access.*;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import java.io.IOException;

// Disable for now...
//@Extension
public class TestExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(TestExtension.class);

    private AS400 as400;
    private SystemStatus systemStatus;

    @Override
    public boolean isSupported() {

        String osArch = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();

        System.err.println("OS Arch: " + osArch);
        System.err.println("OS Name: " + osName);

        try {
            //as400 = new AS400("localhost", "CURRENT");
            as400 = new AS400("10.32.64.142");
            systemStatus = new SystemStatus(as400);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }

        if(as400.isLocal()) {
            log.info("as400 isLocal() true");
        } else {
            log.info("as400 isLocal() FALSE");
        }


        return true;
    }

    @Override
    public String getName() {
        return "ibmi-test";
    }

    @Override
    public String getProvides() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "IBM i Test Extension";
    }

    @Override
    public MetricResult getMetrics() {

        if(systemStatus == null) {
            log.warn("getMetrics() - no system or status");
            return null;
        }

        try {
            int jobsInSystem = systemStatus.getJobsInSystem();
            log.info("Jobs In System: " + jobsInSystem);

            int batchJobsRunning = systemStatus.getBatchJobsRunning();
            log.info("Batch Jobs Running: " + batchJobsRunning);

            int activeThreads = systemStatus.getActiveThreadsInSystem();
            log.info("Active Threads: " + activeThreads);

            int activeJobs = systemStatus.getActiveJobsInSystem();
            log.info("Active Jobs: " + activeJobs);

            int onlineUsers = systemStatus.getUsersCurrentSignedOn();
            log.info("Online Users: " + onlineUsers);

        } catch (AS400SecurityException | ErrorCompletingRequestException | InterruptedException | IOException | ObjectDoesNotExistException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
