# AIX Notes

## Installation

```shell
rpm -i --ignoreos sysmon-client.rpm sysmon-plugins.rpm
```

## Run automatically at boot

```shell
mkitab 'sysmon:2:respawn:env JAVA_HOME=/usr/java8_64 /opt/sysmon/client/bin/client -s http://10.20.30.40:9925/metrics >/tmp/sysmon.log 2>&1'
```
