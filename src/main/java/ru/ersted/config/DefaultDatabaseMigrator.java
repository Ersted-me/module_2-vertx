package ru.ersted.config;

import io.vertx.core.json.JsonObject;
import org.flywaydb.core.Flyway;


public class DefaultDatabaseMigrator {

    public static void migrate(JsonObject config) {
        JsonObject dbConfig = config.getJsonObject("db");

        String jdbcUrl = "jdbc:postgresql://" +
                dbConfig.getString("host") + ":" +
                dbConfig.getInteger("port") + "/" +
                dbConfig.getString("database");

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, dbConfig.getString("user"), dbConfig.getString("password"))
                .load();

        flyway.migrate();
    }

}
