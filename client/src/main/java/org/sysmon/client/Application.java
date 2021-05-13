/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.sysmon.client;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "sysmon-client", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @CommandLine.Option(names = { "-s", "--server-url" }, description = "Server URL (default: ${DEFAULT-VALUE}).", defaultValue = "http://127.0.0.1:9925/metrics", paramLabel = "<url>")
    private URL serverUrl;

    @CommandLine.Option(names = { "-n", "--hostname" }, description = "Client hostname (default: <hostname>).", paramLabel = "<name>")
    private String hostname;

    @CommandLine.Option(names = { "-p", "--plugin-dir" }, description = "Plugin jar path (default: ${DEFAULT-VALUE}).", paramLabel = "<path>", defaultValue = "/opt/sysmon/plugins")
    private String pluginPath;

    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws IOException {

        if(hostname == null || hostname.isEmpty()) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                log.warn(e.getMessage());
                hostname = "unknown";
            }
        }

        String pf4jPluginsDir = System.getProperty("pf4j.pluginsDir");
        if(pf4jPluginsDir != null) {
            pluginPath = pf4jPluginsDir;
        }

        Main main = new Main();
        main.bind("pluginPath", pluginPath);
        main.bind("myServerUrl", serverUrl.toString());
        main.bind("myHostname", hostname);
        main.configure().addRoutesBuilder(ClientRouteBuilder.class);

        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        try {
            main.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return 0;

    }

}

