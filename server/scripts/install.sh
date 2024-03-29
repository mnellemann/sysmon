#!/bin/sh

install_config() {
    test -f "/etc/${service_name}.toml" || cp "${config_source}" "/etc/${service_name}.toml"
}

install_systemd() {
    sysctl=$(command -v deb-systemd-invoke || echo systemctl)
    test -f "/etc/systemd/system/${service_name}.service" || cp "${systemd_source}" "/etc/systemd/system/${service_name}.service"
    $sysctl --system daemon-reload >/dev/null || true
    if ! $sysctl is-enabled ${service_name} >/dev/null
    then
        $sysctl enable ${service_name} >/dev/null || true
        $sysctl start ${service_name} >/dev/null || true
    else
        $sysctl restart ${service_name} >/dev/null || true
    fi
}

install_sysv_linux() {
    echo "WARN: No support for ${service_name} on SysV Linux"
}

install_sysv_aix() {
    test -f "/etc/rc.d/init.d/${service_name}" || cp "${sysv_source}" "/etc/rc.d/init.d/${service_name}"
    chmod 0755 "/etc/rc.d/init.d/${service_name}"
    ln -sf /etc/rc.d/init.d/sysmon-client /etc/rc.d/rc2.d/Ssysmon-client
    ln -sf /etc/rc.d/init.d/sysmon-client /etc/rc.d/rc2.d/Ksysmon-client
    echo "Edit /etc/rc.d/init.d/${service_name} and modify server URL and other options"
}

install_sysv() {
    if [ x$(uname | grep AIX) = x"" ]; then
        install_sysv_linux
    else
        install_sysv_aix
    fi
}

# Install configuration file
install_config

# Detect if we are running on a systemd based Linux
if [ x$(command -v systemctl) = x"" ]; then
    install_sysv
else
    install_systemd
fi

exit 0
