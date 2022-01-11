/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package sysmon.client;

import org.apache.camel.main.Main;
import org.slf4j.impl.SimpleLogger;
import picocli.CommandLine;

import java.io.File;
import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "sysmon-client", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    @CommandLine.Option(names = { "-s", "--server-url" }, description = "Server URL (default: ${DEFAULT-VALUE}).", defaultValue = "http://127.0.0.1:9925/metrics", paramLabel = "<url>")
    private URL serverUrl;

    @CommandLine.Option(names = { "-n", "--hostname" }, description = "Client hostname (default: <hostname>).", paramLabel = "<name>")
    private String hostname;

    @CommandLine.Option(names = { "-p", "--plugin-dir" }, description = "Plugin jar path (default: ${DEFAULT-VALUE}).", paramLabel = "<path>", defaultValue = "/opt/sysmon/plugins")
    private String pluginPath;

    @CommandLine.Option(names = { "-c", "--conf" }, description = "Configuration file [default: '/etc/sysmon-client.toml'].", paramLabel = "<file>", defaultValue = "/etc/sysmon-client.toml")
    private File configurationFile;

    @CommandLine.Option(names = { "-d", "--debug" }, description = "Enable debugging (default: ${DEFAULT_VALUE}).")
    private boolean enableDebug = false;


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() {

        String sysmonDebug = System.getProperty("sysmon.debug");
        if(sysmonDebug != null || enableDebug) {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        }

        String sysmonCfgFile = System.getProperty("sysmon.cfgFile");
        if(sysmonCfgFile != null) {
            configurationFile = new File(sysmonCfgFile);
        }

        String sysmonPluginsDir = System.getProperty("sysmon.pluginsDir");
        if(sysmonPluginsDir != null) {
            pluginPath = sysmonPluginsDir;
        }

        if(hostname == null || hostname.isEmpty()) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                System.err.println("Could not detect hostname. Use the '-n' or '--hostname' option to specify.");
                return -1;
            }
        }

        Configuration configuration = new Configuration();

        if(configurationFile.exists()) {
            try {
                configuration.parse(configurationFile.toPath());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return 1;
            }
        }

        Main main = new Main();
        main.bind("pluginPath", pluginPath);
        main.bind("myServerUrl", serverUrl.toString());
        main.bind("myHostname", hostname);
        main.bind("configuration", configuration);
        main.configure().addRoutesBuilder(ClientRouteBuilder.class);

        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        try {
            main.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 1;
        }

        return 0;

    }

}

