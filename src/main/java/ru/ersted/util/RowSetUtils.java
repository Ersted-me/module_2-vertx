package ru.ersted.util;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.ersted.repository.mapper.RowMapper;

import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RowSetUtils {


    public static <T> Optional<T> firstRow(RowSet<Row> rows, RowMapper<T> mapper) {
        RowIterator<Row> iterator = rows.iterator();
        return iterator.hasNext() ? Optional.of(mapper.map(iterator.next()))
                : Optional.empty();
    }

    public static boolean noRowsAffected(RowSet<?> rows) {
        return rows == null || rows.rowCount() == 0;
    }

    public static Void requireRowsAffected(RowSet<?> rows,
                                           Supplier<? extends RuntimeException> exSupplier) {
        if (noRowsAffected(rows)) {
            throw exSupplier.get();
        }
        return null;
    }

}
