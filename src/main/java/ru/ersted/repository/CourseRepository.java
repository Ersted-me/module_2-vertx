package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.model.Course;
import ru.ersted.repository.constant.CourseColumn;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowMapperUtils;
import ru.ersted.util.RowSetUtils;

import java.util.List;
import java.util.Optional;

import static ru.ersted.repository.query.CourseQueryProvider.*;


@RequiredArgsConstructor
public class CourseRepository {

    private final Pool client;

    private final RowMapper<Course> rowMapper;


    public Future<Course> save(Course entity) {
        return client.preparedQuery(INSERT)
                .execute(Tuple.of(entity.getTitle(), entity.getTeacherId()))
                .map(rows -> {
                    entity.setId(rows.iterator().next().getLong(CourseColumn.ID));
                    return entity;
                });
    }

    public Future<List<Course>> getAll(int limit, int offset) {
        return client.preparedQuery(SELECT_ALL)
                .execute(Tuple.of(limit, offset))
                .map(rows -> RowMapperUtils.toList(rows, rowMapper));
    }

    public Future<Optional<Course>> assigningTeacher(SqlConnection connection, Long coursesId, Long teacherId) {
        return connection.preparedQuery(UPDATE_TEACHER_ID)
                .execute(Tuple.of(teacherId, coursesId))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper));
    }

    public Future<Optional<Course>> findById(Long coursesId) {
        return client.preparedQuery(SELECT_BY_ID)
                .execute(Tuple.of(coursesId))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper));
    }

    public Future<Optional<Course>> findById(SqlConnection connection, Long coursesId) {
        return connection.preparedQuery(SELECT_BY_ID)
                .execute(Tuple.of(coursesId))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper));
    }

    public Future<List<Course>> findByTeacherId(Long teacherId) {
        return client.preparedQuery(FIND_BY_TEACHER_ID)
                .execute(Tuple.of(teacherId))
                .map(rows -> RowMapperUtils.toList(rows, rowMapper));
    }

}
