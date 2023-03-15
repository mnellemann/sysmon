package sysmon.plugins.power;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class PowerPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(PowerPlugin.class);

    public PowerPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}
