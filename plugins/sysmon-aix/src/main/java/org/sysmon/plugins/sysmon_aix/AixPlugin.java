package org.sysmon.plugins.sysmon_aix;

import org.pf4j.PluginState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class AixPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(AixPlugin.class);

    public AixPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        if(!System.getProperty("os.name").toLowerCase().contains("aix")) {
            log.warn("start() - Plugin not supported here.");
            wrapper.setPluginState(PluginState.DISABLED);
            wrapper.getPlugin().stop();
        } else {
            log.info("start() - Good to go.");
        }
    }

    @Override
    public void stop() {
        log.debug("stop()");
    }


}


