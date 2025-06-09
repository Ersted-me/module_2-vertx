package ru.ersted.config.database;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.ConfigKeys;

@Slf4j
public class DefaultDatabaseClientFactory {

    public static Pool createClient(Vertx vertx, JsonObject appConfig) {
        DatabaseConfig config = new DatabaseConfig(appConfig.getJsonObject(ConfigKeys.DATABASE));

        log.info("Creating database connection pool to {}:{} (database: {})",
                config.getHost(),
                config.getPort(),
                config.getDatabase()
        );

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(config.getHost())
                .setPort(config.getPort())
                .setDatabase(config.getDatabase())
                .setUser(config.getUser())
                .setPassword(config.getPassword())
                .setSslMode(config.getSslMode());

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(config.getMaxPoolSize());

        return Pool.pool(vertx, connectOptions, poolOptions);
    }

}
