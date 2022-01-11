# AIX Notes

Works on IBM Power VIO (Virtual IO) servers, as well as regular IBM Power AIX installations.

## Installation

We require Java 8, which should already be installed on AIX, or is available to install. 
The RPM packages are *"noarch"* Java bytecode, so we can use the **--ignoreos** option to install:

```shell
rpm -i --ignoreos sysmon-client.rpm sysmon-plugins.rpm
```

## Run automatically at boot

See the [sysv-init.md](sysv-init.md) file for instructions.