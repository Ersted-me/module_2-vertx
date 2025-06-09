package ru.ersted.config.server;

import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ServerConfig {
    private static final int DEFAULT_HTTP_PORT    = 8080;
    private static final int DEFAULT_HEALTH_PORT  = 8081;
    private static final int DEFAULT_METRICS_PORT = 8082;

    private final int httpPort;
    private final int healthPort;
    private final int metricsPort;

    public ServerConfig(JsonObject json) {
        JsonObject src = json == null ? new JsonObject() : json;

        this.httpPort    = readPort(src, "http",    DEFAULT_HTTP_PORT);
        this.healthPort  = readPort(src, "health",  DEFAULT_HEALTH_PORT);
        this.metricsPort = readPort(src, "metrics", DEFAULT_METRICS_PORT);

        log.info("Server config loaded: httpPort: '{}', healthPort: '{}', metricsPort: '{}'",
                httpPort, healthPort, metricsPort);
    }

    private int readPort(JsonObject root, String section, int def) {
        return root.getJsonObject(section, new JsonObject())
                .getInteger("port", def);
    }

}
