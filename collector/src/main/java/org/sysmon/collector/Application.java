package org.sysmon.collector;

import org.apache.camel.main.Main;

public class Application {

    public static void main(String[] args) {
        Main main = new Main();
        main.configure().addRoutesBuilder(CollectorRouteBuilder.class);

        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        try {
            main.run(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
