# System Monitor

Java based system monitoring solution with support for plugins.

- Example Grafana [dashboard](https://bitbucket.org/mnellemann/sysmon/downloads/sysmon-example-dashboard.png) showing metrics from a host running *sysmon*.



## Known problems

### Correct timezone and clock

- Ensure you have **correct timezone and date/time** and NTPd (or similar) running to keep it accurate!

### Naming collision

You can't have hosts with the same name, as these cannot be distinguished when metrics are
written to InfluxDB (which uses the hostname as key).

### Renaming hosts

If you rename a host, the metrics in InfluxDB will still be available by the old hostname, and new metrics will be written with the new hostname. There is no easy way to migrate the old data, but you can delete it easily:

```text
USE sysmon;
DELETE WHERE hostname = 'unknown';
```

## Components 

### Client

Runs on your hosts and collects metrics, which are sent to the central *server*.


### Server

Receives aggregated metrics from clients and saves these into InfluxDB.


### Plugins

Loaded by the client and provides extensions for doing the actual collecting of metrics.