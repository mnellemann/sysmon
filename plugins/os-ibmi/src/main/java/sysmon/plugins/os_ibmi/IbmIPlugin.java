package sysmon.plugins.os_ibmi;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class IbmIPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(IbmIPlugin.class);

    private static SystemStatus systemStatus;
    private static AS400 as400;

    public IbmIPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }


    public static SystemStatus getSystemStatus() {

        try {
            if (as400 == null) {
                as400 = IbmIPlugin.getAS400();
            }
            if(systemStatus == null) {
                systemStatus = new SystemStatus(as400);
            }
        } catch (Exception exception) {
            log.error("getSystemStatus() - {}", exception.getMessage());
            return null;
        }

        return systemStatus;
    }


    public static AS400 getAS400() {

        String osArch = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();

        if(!osArch.equals("ppc64") && !osName.equals("os/400")) {
            log.info("getAS400() - OS Arch: {}", osArch);
            log.info("getAS400() - OS Name: {}", osName);
            return null;
        }

        try {
            as400 = new AS400("localhost", "*CURRENT");
            //as400 = new AS400("10.32.64.142");
        } catch (Exception exception) {
            log.error("getAS400() - {}", exception.getMessage());
            return null;
        }

        return as400;
    }

}


