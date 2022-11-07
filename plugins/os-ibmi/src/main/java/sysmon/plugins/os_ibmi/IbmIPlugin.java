package sysmon.plugins.os_ibmi;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class IbmIPlugin extends Plugin {

    private static final Logger log = LoggerFactory.getLogger(IbmIPlugin.class);

    private static AS400 as400;
    private static SystemStatus systemStatus;
    private static Connection connection;


    public IbmIPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public static AS400 getAS400() {

        if(as400 != null) {
            return as400;
        }

        // Check platform
        String osArch = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();
        if(!osArch.equals("ppc64") && !osName.equals("os/400")) {
            log.info("getAS400() - OS Arch: {}", osArch);
            log.info("getAS400() - OS Name: {}", osName);
            return null;
        }

        try {
            as400 = new AS400("localhost", "*CURRENT");
            //as400 = new AS400("localhost", "*LOCAL");
            //as400 = new AS400("10.32.64.142");
            return as400;
        } catch (Exception exception) {
            log.error("getAS400() - {}", exception.getMessage());
        }

        return null;
    }


    public static SystemStatus getSystemStatus() {

        if(systemStatus != null) {
            return systemStatus;
        }

        try {
            if (as400 == null) {
                as400 = IbmIPlugin.getAS400();
            }
            if(systemStatus == null && as400 != null) {
                systemStatus = new SystemStatus(as400);
                return systemStatus;
            }
        } catch (Exception exception) {
            log.error("getSystemStatus() - {}", exception.getMessage());
        }

        return null;
    }


    public static Connection getConnection() {

        if(connection != null) {
            return connection;
        }

        try {
            DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
            connection = DriverManager.getConnection("jdbc:as400://localhost");
            return connection;
        } catch (SQLException exception) {
            log.error("getConnection() - {}", exception.getMessage());
        }

        return null;
    }


}


