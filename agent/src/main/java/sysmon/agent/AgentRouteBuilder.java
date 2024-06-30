package sysmon.agent;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.spi.Registry;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sysmon.shared.ComboResult;
import sysmon.shared.MetricExtension;
import sysmon.shared.MetricResult;

import javax.script.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentRouteBuilder extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(AgentRouteBuilder.class);

    private final Set<String> scriptFiles = new HashSet<>();


    @Override
    public void configure() {

        Registry registry = getContext().getRegistry();
        Configuration configuration = (Configuration) registry.lookupByName("configuration");

        Path[] pluginPaths = { Paths.get(registry.lookupByNameAndType("pluginPath", String.class)) };
        PluginManager pluginManager = new JarPluginManager(pluginPaths);
        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        List<MetricExtension> metricExtensions = pluginManager.getExtensions(MetricExtension.class);
        for (MetricExtension ext : metricExtensions) {
            final String name = ext.getName();

            // Load configuration if available
            if(configuration.isForExtension(name)) {
                log.info("Loading configuring for extension: " + ext.getDescription());
                ext.setConfiguration(configuration.getForExtension(name));
            }

            if(ext.isSupported() && ext.isEnabled()) {
                addExtensionRoute(ext);
            } else {
                log.info("Skipping extension (not supported or disabled): {}", ext.getDescription());
            }
        }

        from("seda:metrics?purgeWhenStopping=true")
                .routeId("aggregation")
                .aggregate(constant(true), AggregationStrategies.beanAllowNull(ComboAppender.class, "append"))
                .completionTimeout(5000L)
                .doTry()
                    .to("seda:outbound?discardWhenFull=true")
                    .log("Aggregating ${body} before sending to server.")
                .doCatch(Exception.class)
                    .log(LoggingLevel.WARN, "Error: ${exception.message}.")
                .end();

        from("seda:outbound?purgeWhenStopping=true")
                .routeId("outbound")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .doTry()
                    .marshal(new JacksonDataFormat(ComboResult.class))
                    .to((String)registry.lookupByName("myServerUrl") + "?responseTimeout=1000")
                    .log("${body}")
                .doCatch(Exception.class)
                    .log(LoggingLevel.WARN,"Error: ${exception.message}.")
                .end();

        // Find all local scripts
        String scriptsPath = configuration.getScriptPath();
        if(scriptsPath != null && Files.isDirectory(Paths.get(scriptsPath))) {
            try {
                scriptFiles.addAll(listFilesByExtension(scriptsPath, "groovy"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Enable the local scripts
        for (String scriptFile : scriptFiles) {
            try {
                ScriptWrapper scriptWrapper = new ScriptWrapper(scriptsPath, scriptFile);
                addScriptRoute(scriptWrapper);
            } catch(Exception e) {
                log.error("configure() - script error: {}", e.getMessage());
            }
        }

    }


    void addScriptRoute(ScriptWrapper script) {
        Registry registry = getContext().getRegistry();

        from("timer:scripts?fixedRate=true&period=30s")
            .routeId(script.toString())
            .bean(script, "run")
            .outputType(MetricResult.class)
            .process(new MetricEnrichProcessor(registry))
            .choice().when(exchangeProperty("skip").isEqualTo(true))
            .log(LoggingLevel.WARN, "Skipping empty measurement.")
            .stop()
            .otherwise()
            .log("${body}")
            .to("seda:metrics?discardWhenFull=true");
    }


    void addExtensionRoute(MetricExtension ext) {

        Registry registry = getContext().getRegistry();

        // Setup Camel route for this extension
        // a unique timer name gives the timer its own thread, otherwise it's a shared thread for other timers with same name.
        String timerName = ext.isThreaded() ? ext.getName() : "default";
        String timerInterval = (ext.getInterval() != null) ? ext.getInterval() : "30s";
        from("timer:" + timerName + "?fixedRate=true&period=" + timerInterval)
            .routeId(ext.getName())
            .bean(ext, "getMetrics")
            .outputType(MetricResult.class)
            .process(new MetricEnrichProcessor(registry))
            .choice().when(exchangeProperty("skip").isEqualTo(true))
            .log(LoggingLevel.WARN, "Skipping empty measurement.")
            .stop()
            .otherwise()
            .log("${body}")
            .to("seda:metrics?discardWhenFull=true");
    }


    List<String> findScripts(String location) {
        log.info("Looking for scripts in: {}", location);
        List<String> scripts = new ArrayList<>();
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factoryList = manager.getEngineFactories();
        for (ScriptEngineFactory factory : factoryList) {
            log.info("findScripts() - Supporting: {}", factory.getLanguageName());
            for(String ex : factory.getExtensions()) {
                log.info("findScripts() - Extension: {}", ex);
                try {
                    scripts.addAll(listFilesByExtension(location, ex));
                    log.warn(scripts.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return scripts;
    }


    Set<String> listFilesByExtension(String dir, String ext) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(s -> s.endsWith(ext))
                .collect(Collectors.toSet());
        }
    }

}
