# Client

This is the client component of SysMon. Install on the hosts for which you want to collect metrics. 

## Installation

Download *.deb* or *.rpm* packages for sysmon-client *and* sysmon-plugins, and install.

See the [doc/systemd.md](doc/systemd.md) or [doc/sysv-init.md](doc/sysv-init.md) files for further instructions.

## Development

### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```
