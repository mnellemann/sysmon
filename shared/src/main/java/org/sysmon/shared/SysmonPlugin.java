package org.sysmon.shared;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysmonPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(SysmonPlugin.class);

    public SysmonPlugin(PluginWrapper wrapper) {
        super(wrapper);
        log.warn("SysmonPlugin");
    }

    @Override
    public void start() {
        log.warn("start();");
    }



}
