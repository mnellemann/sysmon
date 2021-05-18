# System Monitor / System Observer

... or some better name.


## Client

Runs on your hosts and collects metrics. Metrics are aggregated and sent to the central *server*.


## Server

Receives aggregated measurements from clients and saves metrics into InfluxDB.


## Plugins

Loaded by the client and provides extensions for doing the actual metric monitoring.