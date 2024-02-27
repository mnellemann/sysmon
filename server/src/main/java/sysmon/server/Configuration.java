package sysmon.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Path;

public final class Configuration {

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    private TomlParseResult result;

    void parse(Path configurationFile) throws IOException {
        log.info("Parsing configuration file: " + configurationFile);
        result = Toml.parse(configurationFile);
        result.errors().forEach(error -> log.error(error.toString()));
    }

    TomlParseResult result() {
        return result;
    }

}

