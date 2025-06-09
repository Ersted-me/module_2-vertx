package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.exception.NotFoundException;
import ru.ersted.model.Teacher;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowMapperUtils;
import ru.ersted.util.RowSetUtils;

import java.util.List;

import static ru.ersted.repository.query.TeacherQueryProvider.*;

@RequiredArgsConstructor
public class TeacherRepository {

    private final Pool client;

    private final RowMapper<Teacher> rowMapper;


    public Future<Teacher> save(Teacher entity) {

        return client.preparedQuery(SAVE_TEACHER_SQL)
                .execute(Tuple.of(entity.getName()))
                .map(rows -> {
                    entity.setId(rows.iterator().next().getLong("id"));
                    return entity;
                });
    }

    public Future<List<Teacher>> getAll(int limit, int offset) {

        return client.preparedQuery(GET_ALL_TEACHER_SQL)
                .execute(Tuple.of(limit, offset))
                .map(rows -> RowMapperUtils.toList(rows, rowMapper));
    }

    public Future<Teacher> findById(Long teacherId) {

        return client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(teacherId))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper)
                        .orElseThrow(() -> new NotFoundException("Teacher with id '%s' not found".formatted(teacherId)))
                );
    }

}
