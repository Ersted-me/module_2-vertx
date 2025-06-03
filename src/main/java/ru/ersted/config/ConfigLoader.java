package ru.ersted.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ConfigLoader {

    private final static String DEFAULT_CONFIG_FILE_PROPERTY_KEY = "config.file";
    private final static String DEFAULT_CONFIG_FILE = "src/main/resources/application-config.json";

    public static Future<JsonObject> load(Vertx vertx) {
        Promise<JsonObject> promise = Promise.promise();

        ConfigRetriever retriever = ConfigRetriever.create(vertx, buildRetrieverOptions());

        retriever.getConfig(ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result());
            } else {
                promise.fail("Config loading failed: " + ar.cause().getMessage());
            }
        });

        return promise.future();
    }

    private static ConfigRetrieverOptions buildRetrieverOptions() {
        return new ConfigRetrieverOptions()
                .addStore(buildFileStore())
                .addStore(buildEnvStore());
    }

    private static ConfigStoreOptions buildFileStore() {
        String configFilePath = getConfigFilePath();
        return new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", configFilePath));
    }

    private static ConfigStoreOptions buildEnvStore() {
        return new ConfigStoreOptions()
                .setType("env");
    }

    private static String getConfigFilePath() {
        return System.getProperty(DEFAULT_CONFIG_FILE_PROPERTY_KEY, DEFAULT_CONFIG_FILE);
    }

}
