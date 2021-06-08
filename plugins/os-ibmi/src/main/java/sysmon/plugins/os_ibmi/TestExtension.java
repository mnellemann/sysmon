package sysmon.plugins.os_ibmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

public class TestExtension implements MetricExtension {

    private static final Logger log = LoggerFactory.getLogger(TestExtension.class);


    @Override
    public boolean isSupported() {

        String osArch = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();

        System.err.println("OS Arch: " + osArch);
        System.err.println("OS Name: " + osName);

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
        return null;
    }
}
