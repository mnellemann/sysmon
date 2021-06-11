# System Monitor

Java based system monitoring solution with support for plugins.

- Example Grafana [dashboard](https://bitbucket.org/mnellemann/sysmon/downloads/sysmon-example-dashboard.png) showing metrics from a host running *sysmon*.

## Components 

### Client

Runs on your hosts and collects metrics, which are sent to the central *server*.


### Server

Receives aggregated metrics from clients and saves these into InfluxDB.


### Plugins

Loaded by the client and provides extensions for doing the actual collecting of metrics.