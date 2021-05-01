package org.sysmon.shared;

import org.pf4j.ExtensionPoint;

public interface MetricExtension extends ExtensionPoint {

    boolean isSupported();
    String getGreeting();
    MetricResult getMetrics();

}
