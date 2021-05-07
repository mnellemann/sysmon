package org.sysmon.server;

import org.apache.camel.main.Main;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "sysmon-server", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    @CommandLine.Option(names = { "-i", "--influxdb-url" }, description = "InfluxDB URL [default: 'http://localhost:8086'].", defaultValue = "http://localhost:8086", paramLabel = "<url>")
    private URL influxUrl;

    @CommandLine.Option(names = { "-u", "--influxdb-user" }, description = "InfluxDB User [default: 'root'].", defaultValue = "root", paramLabel = "<user>")
    private String influxUser;

    @CommandLine.Option(names = { "-p", "--influxdb-pass" }, description = "InfluxDB Password [default: ''].", defaultValue = "", paramLabel = "<pass>")
    private String influxPass;

    @CommandLine.Option(names = { "-l", "--listen-port" }, description = "Listening port [default: '9925'].", defaultValue = "9925", paramLabel = "<port>")
    private String listenPort;


    public static void main(String... args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }


    @Override
    public Integer call() throws IOException {

        InfluxDB influxConnectionBean = InfluxDBFactory.connect(influxUrl.toString(), influxUser, influxPass);

        Main main = new Main();
        main.bind("myInfluxConnection", influxConnectionBean);
        main.bind("myListenPort", Integer.parseInt(listenPort));
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
