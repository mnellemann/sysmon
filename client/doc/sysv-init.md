# SysV init Notes

- Copy the *sysmon-client.sh* into *sysmon-client* in the correct location for init scripts on your operating system.
- Edit the file and specify the sysmon-server URL in the *args* variable.
- Edit the file and uncomment *JAVA_HOME* if required
- SymLink to the required run-levels.


## AIX & VIO

```shell
# Remember to edit and set JAVA_HOME to eg. /usr/java8_64
cp sysmon-client.sh /etc/rc.d/init.d/sysmon-client
chmod +x /etc/rc.d/init.d/sysmon-client
ln -s /etc/rc.d/init.d/sysmon-client /etc/rc.d/rc2.d/Ssysmon-client
ln -s /etc/rc.d/init.d/sysmon-client /etc/rc.d/rc2.d/Ksysmon-client
```

