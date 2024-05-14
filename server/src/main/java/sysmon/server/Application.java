package sysmon.server;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.camel.main.Main;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.slf4j.simple.SimpleLogger;

import picocli.CommandLine;

@CommandLine.Command(name = "sysmon-server", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    @CommandLine.Option(names = { "-c", "--conf" }, description = "Configuration file [default: '/etc/sysmon-server.toml'].", paramLabel = "<file>", defaultValue = "/etc/sysmon-server.toml")
    private File configurationFile;

    @CommandLine.Option(names = { "-d", "--debug" }, description = "Enable debugging (default: ${DEFAULT_VALUE}).")
    private Boolean enableDebug = false;


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws IOException {

        String doDebug = System.getProperty("sysmon.debug");
        if(doDebug != null || enableDebug) {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
        }

        String useConfigFile = System.getProperty("sysmon.cfgFile");
        if(useConfigFile != null) {
            configurationFile = new File(useConfigFile);
        }

        Configuration configuration = new Configuration();
        if(configurationFile.exists()) {
            try {
                configuration.parse(configurationFile.toPath());
            } catch (Exception e) {
                System.err.println("Could not parse configuration file: " + e.getMessage());
                return 1;
            }
        }


        String influxUrl = configuration.result().contains("influx.url") ? configuration.result().getString("influx.url") : "http://localhost:8086";
        String influxUser = configuration.result().contains("influx.user") ? configuration.result().getString("influx.user") : "root";
        String influxPass = configuration.result().contains("influx.pass") ? configuration.result().getString("influx.pass") : "";
        InfluxDB influxDB = InfluxDBFactory.connect(influxUrl, influxUser, influxPass);

        Main main = new Main();
        main.bind("configuration", configuration);
        main.bind("myInfluxConnection", influxDB);
        main.bind("http.host", configuration.result().getString("listen"));
        main.bind("http.port", configuration.result().getLong("port").intValue());
        main.bind("threads", configuration.result().getLong("threads").intValue());
        main.bind("dbname", configuration.result().getString("influx.db"));
        main.bind("localTime", configuration.result().getBoolean("localtime"));
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
