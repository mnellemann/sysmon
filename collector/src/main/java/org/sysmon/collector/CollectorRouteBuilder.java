package org.sysmon.collector;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
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
                .type(MetricMessageDTO.class)
                .route()
                .to("bean:incomingMetricProcessor")
                .to("seda:inbound")
                .endRest();

        /*
        from("seda:inbound")
                .to("influxdb://myInfluxConnection?databaseName=sysmon");
         */

    }



}
