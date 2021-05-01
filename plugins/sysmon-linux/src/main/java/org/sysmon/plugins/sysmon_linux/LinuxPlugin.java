package org.sysmon.plugins.sysmon_linux;

import org.pf4j.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;


public class LinuxPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(LinuxPlugin.class);

    public LinuxPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        if(!System.getProperty("os.name").toLowerCase().contains("linux")) {
            log.warn("start() - Plugin not supported here.");
            wrapper.setPluginState(PluginState.DISABLED);
        } else {
            log.info("start() - Good to go.");
        }
    }

    @Override
    public void stop() {
        log.debug("stop()");
    }



}
