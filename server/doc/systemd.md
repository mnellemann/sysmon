# Linux systemd notes

Setup as systemd service to start automatically at boot:

```shell
cp sysmon-server.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable sysmon-server
systemctl restart sysmon-server
```
