package org.sysmon.collector;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.sysmon.collector.processor.MetricResultToPointProcessor;
import org.sysmon.shared.MetricResult;
import org.sysmon.shared.dto.MetricMessageDTO;

public class CollectorRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("jetty")
                .bindingMode(RestBindingMode.auto)
                .host("127.0.0.1")
                .port(9925);

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
                .log("Got metric from: ${header.component}")
                .doTry()
                    .process(new MetricResultToPointProcessor())
                    .log("${body}")
                    .to("influxdb://myInfluxConnection?databaseName=sysmon&retentionPolicy=autogen")
                .doCatch(Exception.class)
                    .log("Error storing metric to InfluxDB: ${exception}")
                .end();


    }



}
