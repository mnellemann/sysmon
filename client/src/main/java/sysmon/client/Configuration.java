package sysmon.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Configuration {

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    private TomlParseResult result;

    void parse(Path configurationFile) throws IOException {
        log.info("Parsing configuration file: " + configurationFile);
        result = Toml.parse(configurationFile);
        result.errors().forEach(error -> log.error(error.toString()));
    }


    boolean isForExtension(String extName) {
        if(result == null) {
            return false;
        }
        String key = String.format("extension.%s", extName);
        return result.contains(key);
    }


    Map<String, Object> getForExtension(String extName) {

        if(result == null) {
            log.debug("No configuration file loaded ...");
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        String key = String.format("extension.%s", extName);
        TomlTable table = result.getTableOrEmpty(key);
        table.keySet().forEach( k -> {
            if(table.isString(k)) {
                map.put(k, table.getString(k));
            } else if(table.isBoolean(k)) {
                map.put(k, table.getBoolean(k));
            } else if(table.isDouble(k)) {
                map.put(k, table.getDouble(k));
            } else if(table.isArray(k)) {
                map.put(k, Objects.requireNonNull(table.getArray(k)).toList());
            }

        });

        return map;
    }

}