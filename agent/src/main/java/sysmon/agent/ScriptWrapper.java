package sysmon.agent;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.MetricResult;
import sysmon.shared.MetricScript;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ScriptWrapper {

    private static final Logger log = LoggerFactory.getLogger(ScriptWrapper.class);

    private final static GroovyClassLoader loader = new GroovyClassLoader();

    private GroovyObject script;
    private final String name;

    public ScriptWrapper(String scriptPath, String scriptFile) {
        name = scriptFile;
        try {
            Class<?> scriptClass = loader.parseClass(new File(scriptPath, scriptFile));
            script = (GroovyObject) scriptClass.getDeclaredConstructor().newInstance();
        } catch (IOException |InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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

    @Override
    public String toString() {
        return name;
    }

}
