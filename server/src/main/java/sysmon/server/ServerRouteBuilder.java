package sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.Registry;
import sysmon.shared.MetricResult;

public class ServerRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        Registry registry = getContext().getRegistry();

        restConfiguration().component("netty-http")
                .bindingMode(RestBindingMode.auto)
                .host(registry.lookupByNameAndType("http.host", String.class))
                .port(registry.lookupByNameAndType("http.port", Integer.class));

        /*
        rest()
                .get("/")
                .produces("text/html")
                .route()
                .to("log:stdout")
                .endRest();
         */

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

        fromF("seda:inbound?concurrentConsumers=%s", registry.lookupByNameAndType("threads", Integer.class))
                .log(">>> metric: ${header.hostname} - ${body}")
                .doTry()
                    .process(new MetricResultToPointProcessor())
                    .toF("influxdb://ref.myInfluxConnection?databaseName=%s&retentionPolicy=autogen", "sysmon")
                .doCatch(Exception.class)
                    .log("Error storing metric to InfluxDB: ${exception}")
                .end();

    }


}
