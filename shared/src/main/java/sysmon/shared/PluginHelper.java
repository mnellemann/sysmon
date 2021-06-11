package sysmon.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
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


    public static InputStream executeCommand(String... cmd) {

        InputStream inputStream = null;
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
            inputStream = process.getInputStream();

            int exitCode = process.waitFor();
            if(exitCode > 0) {
                log.warn("executeCommand() - exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return inputStream;
    }


    public static boolean canExecute(String cmd) {
        return Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> Files.exists(path.resolve(cmd)));
    }


    public static List<String> readFile(String filename) {
        List<String> allLines = new ArrayList<>();
        try {
            allLines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return allLines;
    }

}
