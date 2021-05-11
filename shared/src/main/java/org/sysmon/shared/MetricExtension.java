package org.sysmon.shared;

import org.pf4j.ExtensionPoint;

public interface MetricExtension extends ExtensionPoint {

    boolean isSupported();

    String getName();
    String getProvides();
    String getDescription();

    MetricResult getMetrics();
}
