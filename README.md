# System Monitor

Java based system monitoring solution with support for plugins.


## Client

Runs on your hosts and collects metrics. Metrics are aggregated and sent to the central *server*.


## Server

Receives aggregated measurements from clients and saves metrics into InfluxDB.


## Plugins

Loaded by the client and provides extensions for doing the actual metric monitoring.