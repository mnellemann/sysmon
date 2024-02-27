# Client / Agent

This is the client/agent component of sysmon, which you install (together with sysmon-plugins) on your hosts.

## Installation

Download *.deb* or *.rpm* packages for sysmon-client *and* sysmon-plugins, and install.

## Development

### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```
