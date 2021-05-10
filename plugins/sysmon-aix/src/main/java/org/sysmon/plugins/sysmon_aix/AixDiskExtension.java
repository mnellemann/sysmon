package org.sysmon.plugins.sysmon_aix;

import org.pf4j.Extension;
import org.sysmon.shared.MetricExtension;
import org.sysmon.shared.MetricResult;

@Extension
public class AixDiskExtension implements MetricExtension {

    @Override
    public boolean isSupported() {
        return System.getProperty("os.name").toLowerCase().contains("aix");
    }

    @Override
    public String getName() {
        return "aix-disk";
    }

    @Override
    public String getDescription() {
        return "AIX Disk Metrics (TODO)";
    }

    @Override
    public MetricResult getMetrics() {
        return new MetricResult("disk");
    }

}