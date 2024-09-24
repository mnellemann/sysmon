package sysmon.plugins.unix;

import org.pf4j.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class UnixPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(UnixPlugin.class);

    private static SystemInfo systemInfo;
    private static HardwareAbstractionLayer hardwareAbstractionLayer;


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
