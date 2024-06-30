# Instruction for Linux

Please note that the software versions referenced in this document might have changed and might not be available/working unless updated.

More details are available in the [README.md](../README.md) file.


## Requirements

Java 8 (or later) runtime is required.

## Installation

[Download](https://github.com/mnellemann/sysmon/releases) the latest agent and plugins rpm/deb packages.

### RedHat Linux

```shell
sudo dnf install java-11-openjdk-headless
sudo rpm -ivh sysmon-agent-*.noarch.rpm sysmon-plugins-*.rpm
```

Use *yum* if *dnf* is not available.

### SUSE Linux

```shell
sudo zypper install java-11-openjdk-headless
sudo zypper install sysmon-agent-*.noarch.rpm sysmon-plugins-*.rpm
```

### Debian/Ubuntu Linux

```shell
sudo apt install default-jre-headless
sudo dpkg -i sysmon-agent-*.deb sysmon-plugins-*.deb
```

## Configuration

Edit **/etc/sysmon-agent.toml** and modify the server URL.

Check logs for errors with: ```journalctl -u sysmon-agent```
