package sysmon.shared;

import org.pf4j.ExtensionPoint;

import java.util.Map;

public interface MetricExtension extends ExtensionPoint {

    boolean isEnabled();
    boolean isSupported();

    String getName();
    String getProvides();
    String getDescription();

    void setConfiguration(Map<String, Object> map);

    MetricResult getMetrics() throws Exception;
}
