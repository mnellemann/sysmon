package sysmon.plugins.os_aix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class AixPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(AixPlugin.class);

    public AixPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}


