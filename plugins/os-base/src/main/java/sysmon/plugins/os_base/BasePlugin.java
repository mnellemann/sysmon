package sysmon.plugins.os_base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;


public class BasePlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(BasePlugin.class);

    private static SystemInfo systemInfo;
    private static HardwareAbstractionLayer hardwareAbstractionLayer;

    public BasePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public static HardwareAbstractionLayer getHardwareAbstractionLayer() {

        try {
            if(systemInfo == null) {
                systemInfo = new SystemInfo();
            }
            if(hardwareAbstractionLayer == null) {
                hardwareAbstractionLayer = systemInfo.getHardware();
            }

        } catch (UnsupportedOperationException e) {
            log.warn("getHardwareAbstractionLayer() - {}", e.getMessage());
            return null;
        }

        return hardwareAbstractionLayer;
    }


    public static SystemInfo getSystemInfo() {

        try {
            if(systemInfo == null) {
                systemInfo = new SystemInfo();
            }
            systemInfo.getOperatingSystem();
        } catch (UnsupportedOperationException e) {
            log.warn("getSystemInfo() - {}", e.getMessage());
            return null;
        }

        return systemInfo;
    }

}
