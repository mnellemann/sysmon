# AIX Notes

Works on IBM Power VIO (Virtual IO) servers, as well as regular IBM Power AIX installations.

## Installation

Requires Java 8 (or later), which should already be installed on AIX, or is available to install.
The RPM packages are *"noarch"* Java bytecode, so we can use the **--ignoreos** option to install:

```shell
rpm -ivh --ignoreos sysmon-agent-*.rpm sysmon-plugins-*.rpm
```

## Configuration

Edit **/etc/sysmon-agent.toml** and modify the server URL.

## Upgrades

To upgrade the packages:

```shell
rpm -Uvh --ignoreos sysmon-*.rpm
```

To restart sysmon-agent process after upgrade:

```shell
/etc/rc.d/init.d/sysmon-agent stop; /etc/rc.d/init.d/sysmon-agent start
```
