# AIX Notes

Works on IBM Power VIO (Virtual IO) servers, as well as regular IBM Power AIX installations.

## Installation

We require Java 8, which should already be installed. 
The RPM packages are *"noarch"* Java bytecode, so we can use the **--ignoreos** option to install:

```shell
rpm -i --ignoreos sysmon-client.rpm sysmon-plugins.rpm
```

## Run automatically at boot

Change the *sysmon-server* URL for your environment.

```shell
mkitab 'sysmon:2:respawn:env JAVA_HOME=/usr/java8_64 /opt/sysmon/client/bin/client -s http://10.20.30.40:9925/metrics >/tmp/sysmon.log 2>&1'
init q
```
