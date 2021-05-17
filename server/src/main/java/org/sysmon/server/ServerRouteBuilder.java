package org.sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.Registry;
import org.sysmon.shared.MetricResult;

import java.util.Properties;

public class ServerRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        Registry registry = getContext().getRegistry();

        restConfiguration().component("jetty")
                .bindingMode(RestBindingMode.auto)
                .host(registry.lookupByNameAndType("http.host", String.class))
                .port(registry.lookupByNameAndType("http.port", Integer.class));

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
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
                .to("seda:inbound")
                .endRest();

        //from("seda:inbound").log("Got metric from: ${header.component}").to("mock:sink");

        from("seda:inbound")
                .log(">>> metric: ${header.hostname} - ${body}")
                .doTry()
                    .process(new MetricResultToPointProcessor())
                    .to("influxdb://ref.myInfluxConnection?databaseName=sysmon&retentionPolicy=autogen")
                .doCatch(Exception.class)
                    .log("Error storing metric to InfluxDB: ${exception}")
                .end();

    }


}
