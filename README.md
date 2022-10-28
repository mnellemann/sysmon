# System Monitor

Open source system monitoring solution with support for plugins.

![Sysmon Icon](doc/sysmon.png)

This software is free to use and is licensed under the [Apache 2.0 License](LICENSE).

- Example dashboards are provided in the [doc/dashboards/](doc/dashboards/) folder, which can be imported into your Grafana installation.
- Screenshots are available in the [downloads](https://bitbucket.org/mnellemann/sysmon/downloads/) section.


## Components

This software consist of a server and client component.

### Server

The server component receives aggregated metrics from *clients* and saves these into InfluxDB.

- More information and documentation on the [sysmon-server](server/README.md).

### Client & Plugins

The client runs on all or some of your hosts and collects metrics, which are then sent to the central sysmon-server component. Plugins are loaded by the client at startup and should also be installed.

- More information and documentation on the [sysmon-client](client/README.md).
. More information and documentation on the [sysmon-plugins](plugins/README.md).


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
