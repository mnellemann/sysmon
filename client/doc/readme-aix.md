# AIX Notes

Works on IBM Power VIO (Virtual IO) servers, as well as regular IBM Power AIX installations.

## Installation

We require Java 8, which should already be installed on AIX, or is available to install.
The RPM packages are *"noarch"* Java bytecode, so we can use the **--ignoreos** option to install:

```shell
rpm -i --ignoreos sysmon-client.rpm sysmon-plugins.rpm
```

### Run automatically at boot

See the [sysv-init.md](sysv-init.md) file for instructions, or run from inittab:

```shell
mkitab "sysmon:2:respawn:env JAVA_HOME=/usr/java8_64 /opt/sysmon/client/bin/client -s http://10.x.y.z:9925/metrics"
init q
```

## Upgrades

To upgrade the packages:

```shell
rpm -Uvh --ignoreos sysmon-*.noarch.rpm
```

To restart sysmon-client process after upgrade:

```shell
/etc/rc.d/init.d/sysmon-client stop; /etc/rc.d/init.d/sysmon-client start
# or, if running from inittab:
kill -HUP `ps -e -o pid,comm,args | grep sysmon-client | grep java | awk '{print $1}'`
```
