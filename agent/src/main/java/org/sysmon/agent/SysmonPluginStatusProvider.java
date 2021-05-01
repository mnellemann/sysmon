package org.sysmon.agent;

import org.pf4j.PluginStatusProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SysmonPluginStatusProvider implements PluginStatusProvider {

    private static final Logger log = LoggerFactory.getLogger(SysmonPluginStatusProvider.class);

    private final List<String> enabledPlugins = new ArrayList<>();
    private final List<String> disabledPlugins  = new ArrayList<>();


    public SysmonPluginStatusProvider() {
        log.warn("SysmonPluginManager()");
    }


    @Override
    public boolean isPluginDisabled(String pluginId) {
        log.warn("isPluginDisabled() - " + pluginId);
        if (disabledPlugins.contains(pluginId)) {
            return true;
        }

        return !enabledPlugins.isEmpty() && !enabledPlugins.contains(pluginId);
    }

    @Override
    public void disablePlugin(String pluginId) {

        log.warn("disablePlugin() - " + pluginId);

        if (isPluginDisabled(pluginId)) {
            // do nothing
            return;
        }

    }

    @Override
    public void enablePlugin(String pluginId) {

        log.warn("enablePlugin() - " + pluginId);

        if (!isPluginDisabled(pluginId)) {
            // do nothing
            return;
        }

    }

}
