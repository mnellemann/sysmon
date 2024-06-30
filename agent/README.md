# Agent

This is the monitoring agent sysmon, which you install (together with sysmon-plugins) on your hosts.

## Installation

Download *.deb* or *.rpm* packages for sysmon-agent *and* sysmon-plugins, and install.

## Development

### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```
