package ru.ersted.config.verticle;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.config.server.ServerConfig;

@Slf4j
public class HealthVerticle extends AbstractVerticle {

    private final ServiceContainer serviceContainer;
    private final ServerConfig serverConfig;

    public HealthVerticle(ServiceContainer serviceContainer, ServerConfig serverConfig) {
        this.serviceContainer = serviceContainer;
        this.serverConfig = serverConfig;
    }

    @Override
    public void start() {
        var router = Router.router(vertx);
        var healthCheckHandler = HealthCheckHandler.create(vertx);

        healthCheckHandler.register("live", Promise::complete);

        healthCheckHandler.register("ready", promise -> {
            serviceContainer.getDatabaseClient()
                    .getConnection()
                    .onSuccess(conn -> {
                        conn.close();
                        promise.complete();
                    })
                    .onFailure(promise::fail);
        });

        router.get("/health/live").handler(healthCheckHandler);
        router.get("/health/ready").handler(healthCheckHandler);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(serverConfig.getHealthPort(), http -> {
                    if (http.succeeded()) {
                        log.info("Health HTTP server started on port '{}'", http.result().actualPort());
                    } else {
                        log.error("Health HTTP server failed to start", http.cause());
                    }
                });
    }

}
