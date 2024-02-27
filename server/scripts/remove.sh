#!/bin/sh

remove_systemd() {
    echo "Removing ${service_name} for Systemd"
    sysctl=$(command -v deb-systemd-invoke || echo systemctl)
    $sysctl stop ${service_name} >/dev/null || true
    $sysctl disable ${service_name} >/dev/null || true
    test -f "/etc/systemd/system/${service_name}.service" && rm -f "/etc/systemd/system/${service_name}.service"
    $sysctl --system daemon-reload >/dev/null || true
}

remove_sysv_linux() {
    echo "WARN: No support for ${service_name} on SysV Linux"
}

remove_sysv_aix() {
    echo "Removing ${service_name} for AIX"
    /etc/rc.d/init.d/${service_name} stop
    rm -f /etc/rc.d/init.d/sysmon-client /etc/rc.d/rc2.d/Ksysmon-client
    rm -f /etc/rc.d/rc2.d/Ssysmon-client
}

remove_sysv() {
    if [ x$(uname | grep AIX) = x"" ]; then
        remove_sysv_linux
    else
        remove_sysv_aix
    fi
}

# Detect if we are running on a systemd based Linux
if [ x$(command -v systemctl) = x"" ]; then
    remove_sysv
else
    remove_systemd
fi

exit 0
