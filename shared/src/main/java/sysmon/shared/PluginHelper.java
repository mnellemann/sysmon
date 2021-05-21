package sysmon.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PluginHelper {

    private static final Logger log = LoggerFactory.getLogger(PluginHelper.class);

    final static boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");


    public static List<String> executeCommand(String... cmd) {

        List<String> outputLines = new ArrayList<>();

        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c");
        } else {
            builder.command("sh", "-c");
        }

        for(String c : cmd) {
            builder.command().add(c);
        }

        builder.directory(new File(System.getProperty("user.home")));
        try {

            Process process = builder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                outputLines.add(line);
            }

            int exitCode = process.waitFor();
            if(exitCode > 0) {
                log.warn("executeCommand() - exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return outputLines;
    }


    public static boolean canExecute(String cmd) {
        return Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> Files.exists(path.resolve(cmd)));
    }

}
