package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;

@FunctionalInterface
public interface RowMapper<T> {

    T map(Row row);

}
