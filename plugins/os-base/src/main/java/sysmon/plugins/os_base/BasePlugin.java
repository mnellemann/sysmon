package sysmon.plugins.os_base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;


public class BasePlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(BasePlugin.class);

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}
