package sysmon.plugins.os_linux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;


public class LinuxPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(LinuxPlugin.class);

    public LinuxPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}
