package sysmon.server;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.Registry;
import sysmon.shared.MetricResult;

public class ServerRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        final Registry registry = getContext().getRegistry();
        final String dbname = registry.lookupByNameAndType("dbname", String.class);
        final Integer threads = registry.lookupByNameAndType("threads", Integer.class);

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
                .to("seda:inbound?discardWhenFull=true")
                .endRest();

        fromF("seda:inbound?concurrentConsumers=%s", threads)
                .log(">>> metric: ${header.hostname} - ${body}")
                .doTry()
                    .process(new MetricResultToPointProcessor(dbname))
                    .toF("influxdb://ref.myInfluxConnection?batch=true") //&retentionPolicy=autogen
                .doCatch(Exception.class)
                    .log("Error storing metric to InfluxDB: ${exception}")
                .end();

    }


}
