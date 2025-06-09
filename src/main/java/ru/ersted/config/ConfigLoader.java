package ru.ersted.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigLoader {

    private static final String DEFAULT_PROFILE_PROPERTY_KEY = "application.profile";
    private static final String DEFAULT_CONFIG_FILE_PROPERTY_KEY = "config.file";
    private static final String DEFAULT_PROFILE = "default";

    private static final String DEFAULT_CONFIG_FILE_PATTERN = "application-%s.json";
    private static final String DEFAULT_CONFIG_FOLDER = "src/main/resources/";


    public static Future<JsonObject> load(Vertx vertx) {
        Promise<JsonObject> promise = Promise.promise();

        String profile = detectProfile();
        String configFile = getConfigFilePath(profile);

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions()
                        .addStore(buildFileStore(configFile))
                        .addStore(buildEnvStore())
        );

        retriever.getConfig(ar -> {
            if (ar.succeeded()) {
                log.info("Config loaded successfully for profile '{}'", profile);
                promise.complete(ar.result());
            } else {
                log.warn("Failed to load config for profile '{}': {}. Returning empty config.",
                        profile, ar.cause().getMessage());
                promise.complete(new JsonObject());
            }
        });

        return promise.future();
    }


    protected static String detectProfile() {
        String profile = System.getProperty(DEFAULT_PROFILE_PROPERTY_KEY);
        if (profile == null || profile.isBlank()) {
            profile = System.getenv(DEFAULT_PROFILE_PROPERTY_KEY.toUpperCase().replace('.', '_'));
        }
        return (profile == null || profile.isBlank()) ? DEFAULT_PROFILE : profile.trim();
    }


    protected static String getConfigFilePath(String profile) {
        String overridePath = System.getProperty(DEFAULT_CONFIG_FILE_PROPERTY_KEY);
        if (overridePath != null && !overridePath.isBlank()) {
            return overridePath.trim();
        }
        return DEFAULT_CONFIG_FOLDER + String.format(DEFAULT_CONFIG_FILE_PATTERN, profile);
    }


    protected static ConfigStoreOptions buildFileStore(String configFile) {
        return new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", configFile));
    }


    protected static ConfigStoreOptions buildEnvStore() {
        return new ConfigStoreOptions()
                .setType("env");
    }

}
