package ru.ersted.config;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;


@RequiredArgsConstructor
public class TransactionalOperator {

    private final Pool pool;


    public <T> Future<T> transaction(Function<SqlConnection, Future<T>> work) {
        return pool.withTransaction(work);
    }

}
