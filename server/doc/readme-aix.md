# AIX Notes

## Installation

We require Java 8, which should already be installed on AIX, or can be downloaded and installed.
The RPM packages are *"noarch"* Java bytecode, so we can use the **--ignoreos** option to install:

```shell
rpm -ivh --ignoreos sysmon-server-*.rpm
```

You can find InfluxDB and Grafana packaged for AIX on the [https://www.power-devops.com/](https://www.power-devops.com/) website.
