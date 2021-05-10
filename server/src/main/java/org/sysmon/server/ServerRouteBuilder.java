package org.sysmon.server;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.Registry;
import org.sysmon.shared.MetricResult;

public class ServerRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        Registry registry = getContext().getRegistry();

        restConfiguration().component("jetty")
                .bindingMode(RestBindingMode.auto)
                .host("127.0.0.1")
                .port((Integer) registry.lookupByName("myListenPort"));

        rest()
                .get("/")
                .produces("text/html")
                .route()
                .to("log:stdout")
                .endRest();

        rest()
                .post("/metrics")
                .consumes("application/json")
                .produces("text/html")
                .type(MetricResult.class)
                .route()
                .to("seda:inbound")
                .endRest();

        //from("seda:inbound").log("Got metric from: ${header.component}").to("mock:sink");

        from("seda:inbound")
                .log(">>> metric: ${header.hostname} - ${body}")
                .doTry()
                    .process(new MetricResultToPointProcessor())
                    .to("influxdb://myInfluxConnection?databaseName=sysmon&retentionPolicy=autogen")
                .doCatch(Exception.class)
                    .log("Error storing metric to InfluxDB: ${exception}")
                .end();

    }


}
