package sysmon.plugins.os_ibmi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class IbmIPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(IbmIPlugin.class);

    public IbmIPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

}


