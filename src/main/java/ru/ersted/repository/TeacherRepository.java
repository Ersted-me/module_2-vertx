package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import ru.ersted.model.Teacher;
import ru.ersted.repository.mapper.TeacherRowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherRepository {

    private final Pool client;

    public TeacherRepository(Pool databaseClient) {
        this.client = databaseClient;
    }

    public Future<Teacher> save(Teacher entity) {
        Promise<Teacher> promise = Promise.promise();

        final String SAVE_TEACHER_SQL = "INSERT INTO teacher (name) VALUES ($1) RETURNING id";

        client.preparedQuery(SAVE_TEACHER_SQL)
                .execute(Tuple.of(entity.getName()))
                .onSuccess(rows -> {

                    if (rows.iterator().hasNext()) {
                        entity.setId(rows.iterator().next().getLong("id"));
                        promise.complete(entity);
                    } else {
                        promise.fail(new RuntimeException("Could not save teacher"));
                    }

                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<List<Teacher>> getAll(int limit, int offset) {
        Promise<List<Teacher>> promise = Promise.promise();

        final String GET_ALL_TEACHER_SQL = "SELECT * FROM teacher LIMIT $1 OFFSET $2";

        client.preparedQuery(GET_ALL_TEACHER_SQL)
                .execute(Tuple.of(limit, offset))
                .onSuccess(rows -> {
                    List<Teacher> teachers = new ArrayList<>();
                    for (Row row : rows) {
                        Teacher teacher = TeacherRowMapper.map(row);
                        teachers.add(teacher);
                    }
                    promise.complete(teachers);
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Teacher> findById(Long teacherId) {
        Promise<Teacher> promise = Promise.promise();

        final String FIND_BY_ID_SQL = "SELECT * FROM teacher WHERE id = $1";

        client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(teacherId))
                .onSuccess(rows -> {
                    if (rows.iterator().hasNext()) {
                        Teacher teacher = TeacherRowMapper.map(rows.iterator().next());
                        promise.complete(teacher);
                    } else {
                        promise.fail(new RuntimeException("Could not find teacher"));
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }
}
