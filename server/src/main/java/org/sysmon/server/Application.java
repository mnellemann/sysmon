package org.sysmon.server;

import org.apache.camel.main.Main;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;

public class Application {

    public static void main(String[] args) {

        InfluxDB influxConnectionBean = InfluxDBFactory.connect("http://localhost:8086", "root", "");

        Main main = new Main();
        main.bind("myInfluxConnection", influxConnectionBean);
        main.configure().addRoutesBuilder(CollectorRouteBuilder.class);


        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        try {
            main.run(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

}
