package ru.ersted.util;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.ersted.repository.mapper.RowMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RowMapperUtils {

    public static <T> List<T> toList(RowSet<Row> rows, RowMapper<T> mapper) {
        List<T> out = new ArrayList<>(rows.size());
        rows.forEach(r -> out.add(mapper.map(r)));
        return out;
    }

}
