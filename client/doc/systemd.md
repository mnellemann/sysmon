# Linux systemd notes

Edit the *sysmon-client.service* file and change the sysmon-server URL accordingly to your environment.

Setup as systemd service to start automatically at boot:

```shell
cp sysmon-client.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable sysmon-client
systemctl restart sysmon-client
```
