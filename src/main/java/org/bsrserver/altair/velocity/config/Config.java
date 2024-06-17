package org.bsrserver.altair.velocity.config;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {
    private final Toml configToml;

    public Config(Path dataDirectory) {
        // check file exists
        File configFile = new File(dataDirectory.toAbsolutePath().toString(), "config.toml");
        if (!configFile.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.toml")), configFile.toPath());
            } catch (IOException ignored) {
            }
        }

        configToml = new Toml().read(configFile);
    }

    public String getBackendBaseUrl() {
        return configToml.getString("backend.baseurl");
    }

    public String getBackendSecuredClientKey() {
        return configToml.getString("backend.securedClientKey");
    }
}
