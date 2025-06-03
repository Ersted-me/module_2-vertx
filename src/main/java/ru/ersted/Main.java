package ru.ersted;

import io.vertx.core.Vertx;

import ru.ersted.config.ConfigLoader;
import ru.ersted.config.DefaultDatabaseMigrator;

public class Main {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");
        Vertx vertx = Vertx.vertx();

        ConfigLoader.load(vertx)
                .onSuccess(config -> {
                    DefaultDatabaseMigrator.migrate(config);
                    vertx.deployVerticle(new DefaultVerticle(config));
                })
                .onFailure(error -> {
                    System.err.println("Failed to start application: " + error.getMessage());
                });
    }

}