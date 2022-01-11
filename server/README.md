# Server

This is the server component of SysMon.

## Installation

Download *.deb* or *.rpm* package and install.

See the [doc/systemd.md](doc/systemd.md) or [doc/sysv-init.md](doc/sysv-init.md) files for further instructions.

### Influx Database

Create a database for the metrics:

```text
CREATE DATABASE "sysmon" WITH DURATION 90d REPLICATION 1;
```

## Development


### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```

### Local Testing

#### InfluxDB container

Start the InfluxDB container:

```shell
docker run --name=influxdb --rm -d -p 8086:8086 influxdb:1.8-alpine
```

To execute the Influx client from within the container:

```shell
docker exec -it influxdb influx
```

#### Grafana container

Start the Grafana container, linking it to the InfluxDB container:

```shell
docker run --name grafana --link influxdb:influxdb --rm -d -p 3000:3000 grafana/grafana:7.1.3
```

Setup Grafana to connect to the InfluxDB container by defining a new datasource on URL *http://influxdb:8086* named *sysmon*.

