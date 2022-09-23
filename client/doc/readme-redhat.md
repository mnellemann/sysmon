# Instruction for RedHat / CentOS / AlmaLinux Systems

Please note that the software versions referenced in this document might have changed and might not be available/working unless updated.

More details are available in the [README.md](../README.md) file.


## Requirements

Java 8 (or later) runtime is required.

```shell
sudo dnf install java-11-openjdk-headless
```

Use *yum* if *dnf* is not available.


## Installation

[Download](https://bitbucket.org/mnellemann/sysmon/downloads/) the latest client and plugins rpm files and install:

```shell
wget https://bitbucket.org/mnellemann/sysmon/downloads/sysmon-client-1.0.16-1.noarch.rpm
wget https://bitbucket.org/mnellemann/sysmon/downloads/sysmon-plugins-1.0.16-1.noarch.rpm
rpm -ivh sysmon-client-*.noarch.rpm sysmon-plugins-*.noarch.rpm
cp /opt/sysmon/client/doc/sysmon-client.service /etc/systemd/system/
systemctl daemon-reload
```

Now edit the **/etc/systemd/system/sysmon-client.service** file and change the URL so that it points to *your* sysmon-server.

````
# Modify below line in /etc/systemd/system/sysmon-client.service
ExecStart=/opt/sysmon/client/bin/client -s http://10.20.30.40:9925/metrics
````

Now enable and start the sysmon-client service:

```shell
systemctl enable sysmon-client
systemctl start sysmon-client
```

Check logs for errors with: ```journalctl -u sysmon-client```
