# Instruction for Linux

Please note that the software versions referenced in this document might have changed and might not be available/working unless updated.

More details are available in the [README.md](../README.md) file.


## Requirements

Java 8 (or later) runtime is required.

## Installation

[Download](https://github.com/mnellemann/sysmon/releases) the latest client and plugins rpm or deb files.

### RedHat Linux

```shell
sudo dnf install java-11-openjdk-headless
sudo rpm -ivh sysmon-client-*.noarch.rpm sysmon-plugins-*.rpm
```

Use *yum* if *dnf* is not available.

### SUSE Linux

```shell
sudo zypper install java-11-openjdk-headless
sudo zypper install sysmon-client-*.noarch.rpm sysmon-plugins-*.rpm
```

### Debian/Ubuntu Linux

```shell
sudo apt install default-jre-headless
sudo dpkg -i sysmon-client-*.deb sysmon-plugins-*.deb
```

## Configuration

Edit **/etc/systemd/system/sysmon-client.service** and change the URL so that it points to *your* sysmon-server.

````
# Modify below line in /etc/systemd/system/sysmon-client.service
ExecStart=/opt/sysmon/client/bin/client -s http://10.20.30.40:9925/metrics
````

Check logs for errors with: ```journalctl -u sysmon-client```
