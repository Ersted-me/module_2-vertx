package ru.ersted.config.database;

import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.SslMode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class DatabaseConfig {

    private static final int DEFAULT_PORT = 5432;
    private static final int DEFAULT_MAX_POOL_SIZE = 50;
    private static final SslMode DEFAULT_SSL_MODE = SslMode.DISABLE;

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private final int maxPoolSize;
    private final SslMode sslMode;

    public DatabaseConfig(JsonObject json) {
        List<String> errors = new ArrayList<>();

        this.host = getRequiredString(json, "host", errors);
        this.port = json.getInteger("port", DEFAULT_PORT);
        this.database = getRequiredString(json, "database", errors);
        this.user = getRequiredString(json, "user", errors);
        this.password = getRequiredString(json, "password", errors);
        this.maxPoolSize = json.getInteger("maxPoolSize", DEFAULT_MAX_POOL_SIZE);
        this.sslMode = parseSslMode(json.getString("sslMode"), errors);

        if (!errors.isEmpty()) {
            log.error("Invalid database configuration: {}", String.join("; ", errors));
            throw new IllegalArgumentException("Invalid database configuration: " + String.join("; ", errors));
        }

        log.info("Database config loaded: host={}, port={}, database={}, sslMode={}, poolSize={}",
                host, port, database, sslMode, maxPoolSize);
    }

    private String getRequiredString(JsonObject json, String key, List<String> errors) {
        String value = json.getString(key);
        if (value == null || value.isBlank()) {
            errors.add("Missing or empty field: " + key);
        }
        return value;
    }

    private SslMode parseSslMode(String value, List<String> errors) {
        if (value == null || value.isBlank()) {
            return DEFAULT_SSL_MODE;
        }

        try {
            return SslMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add("Invalid sslMode value: " + value);
            return DEFAULT_SSL_MODE;
        }
    }

}
