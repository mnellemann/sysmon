# Client / Agent

This is the client/agent component of sysmon, which you install (together with sysmon-plugins) on the hosts where you want to collect metrics.

## Installation

Download *.deb* or *.rpm* packages for sysmon-client *and* sysmon-plugins, and install.

See the [doc/systemd.md](doc/systemd.md) or [doc/sysv-init.md](doc/sysv-init.md) files for further instructions on running as a system service.

## Development

### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```
