package org.sysmon.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PluginHelper {

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
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return outputLines;
    }



}
