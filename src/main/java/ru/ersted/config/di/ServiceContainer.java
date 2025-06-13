package ru.ersted.config.di;

import io.vertx.sqlclient.Pool;
import lombok.Getter;


@Getter
public final class ServiceContainer {

    private Pool databaseClient;
    private Handlers handlers;
    private Services services;
    private Repositories repositories;

    private ServiceContainer(Builder builder) {
        this.databaseClient = builder.databaseClient;
        this.handlers = builder.handlers;
        this.services = builder.services;
        this.repositories = builder.repositories;
    }


    public static final class Builder {
        private Pool databaseClient;
        private Handlers handlers;
        private Services services;
        private Repositories repositories;

        public Builder databaseClient(Pool client) {
            this.databaseClient = client;
            return this;
        }

        public Builder handlers(Handlers handlers) {
            this.handlers = handlers;
            return this;
        }

        public Builder services(Services services) {
            this.services = services;
            return this;
        }

        public Builder repositories(Repositories repositories) {
            this.repositories = repositories;
            return this;
        }

        public ServiceContainer build() {

            if (databaseClient == null || handlers == null ||
                    services == null || repositories == null) {
                throw new IllegalStateException("All dependencies must be provided");
            }
            return new ServiceContainer(this);
        }

    }

}
