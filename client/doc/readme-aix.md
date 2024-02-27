# AIX Notes

Works on IBM Power VIO (Virtual IO) servers, as well as regular IBM Power AIX installations.

## Installation

We require Java 8, which should already be installed on AIX, or is available to install.
The RPM packages are *"noarch"* Java bytecode, so we can use the **--ignoreos** option to install:

```shell
rpm -ivh --ignoreos sysmon-client-*.rpm sysmon-plugins-*.rpm
```

## Configuration

Edit **/etc/rc.d/init.d/sysmon-client** and change the URL so that it points to *your* sysmon-server.

## Upgrades

To upgrade the packages:

```shell
rpm -Uvh --ignoreos sysmon-*.rpm
```

To restart sysmon-client process after upgrade:

```shell
/etc/rc.d/init.d/sysmon-client stop; /etc/rc.d/init.d/sysmon-client start
```
