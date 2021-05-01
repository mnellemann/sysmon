package org.sysmon.agent;

import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysmonPluginManager extends DefaultPluginManager {

    private static final Logger log = LoggerFactory.getLogger(SysmonPluginManager.class);


    public SysmonPluginManager() {
        super();
        log.warn("SysmonPluginManager()");
    }

    @Override
    protected PluginStatusProvider createPluginStatusProvider() {
        log.warn("createPluginStatusProvider()");
        return new SysmonPluginStatusProvider();
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        log.warn("createExtensionFactory()");
        return new SingletonExtensionFactory();
    }


}
