package org.sysmon.server;

import org.apache.camel.CamelContext;
import org.apache.camel.main.Main;
import org.apache.camel.support.DefaultRegistry;
import org.apache.camel.support.SimpleRegistry;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "sysmon-server", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    @CommandLine.Option(names = { "-i", "--influxdb-url" }, description = "InfluxDB URL (default: ${DEFAULT-VALUE})].", defaultValue = "http://localhost:8086", paramLabel = "<url>")
    private URL influxUrl;

    @CommandLine.Option(names = { "-u", "--influxdb-user" }, description = "InfluxDB Username (default: ${DEFAULT-VALUE})].", defaultValue = "root", paramLabel = "<user>")
    private String influxUser;

    @CommandLine.Option(names = { "-p", "--influxdb-pass" }, description = "InfluxDB Password (default: ${DEFAULT-VALUE}).", defaultValue = "", paramLabel = "<pass>")
    private String influxPass;

    @CommandLine.Option(names = { "-H", "--server-host" }, description = "Server listening address (default: ${DEFAULT-VALUE}).", paramLabel = "<addr>")
    private String listenHost = "0.0.0.0";

    @CommandLine.Option(names = { "-P", "--server-port" }, description = "Server listening port (default: ${DEFAULT-VALUE}).", paramLabel = "<port>")
    private Integer listenPort = 9925;


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws IOException {

        Properties properties = new Properties();
        properties.put("http.host", listenHost);
        properties.put("http.port", listenPort);

        InfluxDB influxConnectionBean = InfluxDBFactory.connect(influxUrl.toString(), influxUser, influxPass);

        Main main = new Main();
        main.bind("myInfluxConnection", influxConnectionBean);
        main.bind("http.host", listenHost);
        main.bind("http.port", listenPort);
        main.bind("properties", properties);
        main.configure().addRoutesBuilder(ServerRouteBuilder.class);

        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        try {
            main.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return 0;

    }

}
