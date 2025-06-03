package ru.ersted.config;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.SslMode;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DefaultDatabaseClientFactory {

    private final static Integer DEFAULT_MAX_POOL_SIZE = 50;

    public static Pool createClient(Vertx vertx, JsonObject appConfig) {
        JsonObject databaseConfig = appConfig.getJsonObject("db");
        PgConnectOptions connectOptions = createConnectOptions(databaseConfig);
        PoolOptions poolOptions = createPoolOptions(databaseConfig);

        return Pool.pool(vertx, connectOptions, poolOptions);
    }

    private static PgConnectOptions createConnectOptions(JsonObject databaseConfig) {
        return new PgConnectOptions()
                .setHost(databaseConfig.getString("host"))
                .setPort(databaseConfig.getInteger("port"))
                .setDatabase(databaseConfig.getString("database"))
                .setUser(databaseConfig.getString("user"))
                .setPassword(databaseConfig.getString("password"))
                .setSslMode(SslMode.DISABLE); // при необходимости;
    }

    private static PoolOptions createPoolOptions(JsonObject databaseConfig) {
        Integer maxPoolSize = databaseConfig.getInteger("maxPoolSize");
        return new PoolOptions()
                .setMaxSize(maxPoolSize != null ? maxPoolSize : DEFAULT_MAX_POOL_SIZE);
    }

}
