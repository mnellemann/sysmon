package sysmon.server;

import org.apache.camel.main.Main;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "sysmon-server", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    @CommandLine.Option(names = { "-i", "--influxdb-url" }, description = "InfluxDB URL (default: ${DEFAULT-VALUE})].", defaultValue = "http://localhost:8086", paramLabel = "<url>")
    private URL influxUrl;

    @CommandLine.Option(names = { "-u", "--influxdb-user" }, description = "InfluxDB Username (default: ${DEFAULT-VALUE})].", defaultValue = "root", paramLabel = "<user>")
    private String influxUser;

    @CommandLine.Option(names = { "-p", "--influxdb-pass" }, description = "InfluxDB Password (default: ${DEFAULT-VALUE}).", defaultValue = "", paramLabel = "<pass>")
    private String influxPass;

    //@CommandLine.Option(names = { "-d", "--influxdb-db" }, description = "InfluxDB Database (default: ${DEFAULT-VALUE}).", defaultValue = "", paramLabel = "<name>")
    //private String influxName = "sysmon";

    @CommandLine.Option(names = { "-H", "--server-host" }, description = "Server listening address (default: ${DEFAULT-VALUE}).", paramLabel = "<addr>")
    private String listenHost = "0.0.0.0";

    @CommandLine.Option(names = { "-P", "--server-port" }, description = "Server listening port (default: ${DEFAULT-VALUE}).", paramLabel = "<port>")
    private Integer listenPort = 9925;

    @CommandLine.Option(names = { "-t", "--threads" }, description = "Threads for processing inbound metrics(default: ${DEFAULT-VALUE}).", paramLabel = "<num>")
    private Integer threads = 5;

    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws IOException {

        /*
        Properties properties = new Properties();
        properties.put("http.host", listenHost);
        properties.put("http.port", listenPort);
*/
        InfluxDB influxConnectionBean = InfluxDBFactory.connect(influxUrl.toString(), influxUser, influxPass);

        Main main = new Main();
        main.bind("myInfluxConnection", influxConnectionBean);
        main.bind("http.host", listenHost);
        main.bind("http.port", listenPort);
        //main.bind("properties", properties);
        main.bind("threads", threads);
        //main.bind("influxdb_name", influxName);
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
