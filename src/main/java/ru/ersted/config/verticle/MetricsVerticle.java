package ru.ersted.config.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.PrometheusScrapingHandler;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.server.ServerConfig;

@Slf4j
public final class MetricsVerticle extends AbstractVerticle {

    private final ServerConfig serverConfig;

    public MetricsVerticle(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void start() {

        Router router = Router.router(vertx);
        router.get("/metrics").handler(PrometheusScrapingHandler.create());

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(serverConfig.getMetricsPort(), ar -> {
                    if (ar.succeeded()) {
                        log.info("Metrics → http://localhost:{}/metrics", ar.result().actualPort());
                    } else {
                        log.error("Metrics server failed", ar.cause());
                    }
                });
    }
}
