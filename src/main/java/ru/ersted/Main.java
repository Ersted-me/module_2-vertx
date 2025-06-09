package ru.ersted;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.ConfigLoader;
import ru.ersted.config.database.DefaultDatabaseMigrator;
import ru.ersted.config.verticle.MainVerticle;

@Slf4j
public class Main {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");

        Vertx vertx = Vertx.vertx();

        ConfigLoader.load(vertx)
                .onSuccess(config -> {
                    DefaultDatabaseMigrator.migrate(config);
                    vertx.deployVerticle(new MainVerticle(config));
                })
                .onFailure(error -> {
                    log.error("Failed to start application: {}", error.getMessage(), error);
                });
    }
}