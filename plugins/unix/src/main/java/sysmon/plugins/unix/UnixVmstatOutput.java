package sysmon.plugins.unix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class UnixVmstatOutput {

    private static final Logger log = LoggerFactory.getLogger(UnixVmstatOutput.class);

    private int running;
    private int blocked;


    public UnixVmstatOutput(String osType, InputStream inputStream) throws IOException {

        switch (osType) {
            case "linux":
                matchLinux(inputStream);
                break;
            case "aix":
                matchAix(inputStream);
                break;
        }

    }


    private void matchLinux(InputStream inputStream) throws IOException {

        String lastLine = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) {
            String line = reader.readLine();
            log.info("matchLinux() - {}", line);
            lastLine = line.trim();
        }

        String[] tokens = lastLine.split("\\s+");
        running = Integer.parseInt(tokens[0]);
        blocked = Integer.parseInt(tokens[1]);

        inputStream.close();
    }


    private void matchAix(InputStream inputStream) throws IOException {

        String lastLine = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) {
            String line = reader.readLine();
            log.info("matchAix() - {}", line);
            lastLine = line.trim();
        }

        String[] tokens = lastLine.split("\\s+");
        running = Integer.parseInt(tokens[0]);
        blocked = Integer.parseInt(tokens[1]);

        inputStream.close();
    }


    public int getRunning() {
        return running;
    }

    public int getBlocked() {
        return blocked;
    }

    public TreeMap<String, String> getTags() {
        return new TreeMap<>();
    }

    public TreeMap<String, Object> getFields() {
        return new TreeMap<String, Object>() {{
            put("running", running);
            put("blocked", blocked);
        }};
    }

}
