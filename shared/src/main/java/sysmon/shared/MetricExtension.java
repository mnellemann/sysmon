package sysmon.shared;

import org.pf4j.ExtensionPoint;

import java.io.IOException;

public interface MetricExtension extends ExtensionPoint {

    boolean isSupported();

    String getName();
    String getProvides();
    String getDescription();

    MetricResult getMetrics() throws Exception;
}
