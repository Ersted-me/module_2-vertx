package ru.ersted;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.ConfigLoader;
import ru.ersted.config.database.DefaultDatabaseMigrator;
import ru.ersted.config.di.AppComponentBuilder;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.config.metrics.VertxFactory;
import ru.ersted.config.server.ServerConfig;
import ru.ersted.config.verticle.ApiVerticle;
import ru.ersted.config.verticle.BackgroundVerticle;
import ru.ersted.config.verticle.HealthVerticle;
import ru.ersted.config.verticle.MetricsVerticle;

@Slf4j
public class Main {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");

        DatabindCodec.mapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        DatabindCodec.mapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Vertx vertx = VertxFactory.createWithMetrics();

        ConfigLoader.load(vertx)
                .compose(config -> vertx.<Void>executeBlocking(p -> {
                                            DefaultDatabaseMigrator.migrate(config);
                                            p.complete();
                                        },
                                        false)
                                .map(config)
                )
                .onSuccess(config -> {
                    ServiceContainer serviceContainer = AppComponentBuilder.build(vertx, config);

                    int cores = Runtime.getRuntime().availableProcessors();

                    DeploymentOptions apiOpts = new DeploymentOptions()
                            .setInstances(cores)
                            .setThreadingModel(ThreadingModel.EVENT_LOOP);

                    DeploymentOptions bgOpts = new DeploymentOptions()
                            .setThreadingModel(ThreadingModel.WORKER)
                            .setWorkerPoolName("background-pool")
                            .setWorkerPoolSize(8)
                            .setInstances(2);

                    vertx.deployVerticle(
                            () -> new BackgroundVerticle(serviceContainer, new ServerConfig(config)), bgOpts, ar -> {
                                if (ar.failed())
                                    log.error("Failed to deploy BackgroundVerticle: {}", ar);
                            });

                    vertx.deployVerticle(
                            () -> new ApiVerticle(serviceContainer, new ServerConfig(config)), apiOpts, ar -> {
                                if (ar.failed())
                                    log.error("Failed to deploy ApiVerticle: {}", ar);
                            });

                    vertx.deployVerticle(() -> new HealthVerticle(serviceContainer, new ServerConfig(config)), apiOpts, ar -> {
                        if (ar.failed())
                            log.error("Failed to deploy HealthVerticle: {}", ar);
                    });

                    vertx.deployVerticle(() -> new MetricsVerticle(new ServerConfig(config)), apiOpts, ar -> {
                        if (ar.failed())
                            log.error("Failed to deploy MetricsVerticle: {}", ar);
                    });

                })
                .onFailure(error -> {
                    log.error("Failed to start application: {}", error.getMessage(), error);
                });
    }
}