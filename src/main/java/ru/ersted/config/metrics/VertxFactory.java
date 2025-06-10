package ru.ersted.config.metrics;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VertxFactory {

    public static Vertx createWithMetrics() {
        var metrics = new MicrometerMetricsOptions()
                .setPrometheusOptions(new VertxPrometheusOptions()
                        .setStartEmbeddedServer(false)
                        .setEnabled(true))
                .setJvmMetricsEnabled(true)
                .setEnabled(true);

        return Vertx.vertx(new VertxOptions().setMetricsOptions(metrics));
    }

}
