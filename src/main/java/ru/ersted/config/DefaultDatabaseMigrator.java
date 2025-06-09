package ru.ersted.config;

import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;


@Slf4j
public class DefaultDatabaseMigrator {

    public static void migrate(JsonObject config) {
        DatabaseConfig dbConfig = new DatabaseConfig(config.getJsonObject(ConfigKeys.DATABASE));

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                dbConfig.getHost(), dbConfig.getPort(), dbConfig.getDatabase());

        log.info("Starting Flyway migration for DB {} at {}:{}", dbConfig.getDatabase(), dbConfig.getHost(), dbConfig.getPort());

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
                    .load();

            flyway.migrate();

            log.info("Flyway migration completed successfully.");
        } catch (Exception e) {
            log.error("Flyway migration failed: {}", e.getMessage(), e);
            throw e;
        }
    }

}
