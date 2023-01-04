package sysmon.client;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.MetricResult;
import sysmon.shared.MetricScript;

import java.io.File;
import java.io.IOException;

public class ScriptWrapper {

    private static final Logger log = LoggerFactory.getLogger(ScriptWrapper.class);

    private final static GroovyClassLoader loader = new GroovyClassLoader();

    private GroovyObject script;

    public ScriptWrapper(String scriptPath, String scriptFile) {
        try {
            Class scriptClass = loader.parseClass(new File(scriptPath, scriptFile));
            script = (GroovyObject) scriptClass.newInstance();
        } catch (IOException |InstantiationException | IllegalAccessException e) {
            log.error("ScriptWrapper() - error: {}", e.getMessage());
        }
    }


    MetricResult run() {
        MetricResult result = null;
        if (script != null && script instanceof MetricScript) {
            result = (MetricResult) script.invokeMethod("getMetrics", null);
        }
        return result;
    }


}
