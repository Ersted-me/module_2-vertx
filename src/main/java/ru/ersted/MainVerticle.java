package ru.ersted;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.ConfigKeys;
import ru.ersted.config.di.builder.AppComponentBuilder;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.config.server.ServerConfig;
import ru.ersted.config.verticle.ApiVerticle;
import ru.ersted.config.verticle.HealthVerticle;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    private final JsonObject config;
    private final ServerConfig serverConfig;


    public MainVerticle(JsonObject config) {
        this.config = config;
        this.serverConfig = new ServerConfig(config.getJsonObject(ConfigKeys.SERVER));
    }

    @Override
    public void start() {
        ServiceContainer serviceContainer = AppComponentBuilder.build(vertx, config);

        deployVerticle(new ApiVerticle(serviceContainer, serverConfig));
        deployVerticle(new HealthVerticle(serviceContainer, serverConfig));

    }

    private void deployVerticle(AbstractVerticle verticle) {
        vertx.deployVerticle(verticle,
                new DeploymentOptions().setConfig(config),
                res -> {
                    if (res.succeeded()) {
                        log.info(verticle.getClass().getSimpleName() + " deployed successfully");
                    } else {
                        log.error("Failed to deploy {}", verticle.getClass().getSimpleName(), res.cause());
                    }
                });
    }

}
