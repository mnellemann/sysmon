# System Monitor

This software is free to use and is licensed under the [Apache 2.0 License](LICENSE).

- Example dashboards are provided in the [doc/](doc) folder, which can be imported into your Grafana installation.
- Screenshots are available in the [downloads](https://bitbucket.org/mnellemann/sysmon/downloads/) section.

![Sysmon Icon](doc/monitoring-eye-svgrepo-com.png)

## Components 

### Client

Runs on your hosts and collects metrics, which are sent to the central *server*.

[More information](client/README.md).

### Server

Receives aggregated metrics from *clients* and saves these into InfluxDB.

[More information](server/README.md).

### Plugins

Loaded by the *client* and provides extensions for doing the actual collecting of metrics.

[More information](plugins/README.md).


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