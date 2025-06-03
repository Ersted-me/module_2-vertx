package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import ru.ersted.model.Course;

import java.util.ArrayList;
import java.util.List;

import static ru.ersted.repository.mapper.CourseRowMapper.map;

public class CourseRepository {
    private final Pool client;

    public CourseRepository(Pool client) {
        this.client = client;
    }


    public Future<Course> save(Course entity) {
        final String SAVE_SQL = "INSERT INTO course (title, teacher_id) VALUES ($1, $2) RETURNING id;";
        return client.preparedQuery(SAVE_SQL)
                .execute(Tuple.of(entity.getTitle(), entity.getTeacherId()))
                .map(rows -> {
                    entity.setId(rows.iterator().next().getLong("id"));
                    return entity;
                });
    }

    public Future<List<Course>> getAll(int limit, int offset) {
        Promise<List<Course>> promise = Promise.promise();

        String query = "SELECT * FROM course LIMIT $1 OFFSET $2";

        client.preparedQuery(query)
                .execute(Tuple.of(limit, offset))
                .onSuccess(rows -> {
                    List<Course> courses = new ArrayList<>();
                    for (Row row : rows) {
                        Course course = map(row);
                        courses.add(course);
                    }
                    promise.complete(courses);
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Void> assigningTeacher(Long coursesId, Long teacherId) {
        Promise<Void> promise = Promise.promise();

        final String ASSIGN_TEACHER_TO_COURSE = "UPDATE course SET teacher_id = $1 WHERE id = $2";

        client.preparedQuery(ASSIGN_TEACHER_TO_COURSE)
                .execute(Tuple.of(teacherId, coursesId))
                .onSuccess(rows -> {
                    if (rows.rowCount() == 0) {
                        promise.fail("Course with id " + coursesId + " does not exist");
                    } else {
                        promise.complete();
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Course> findById(Long coursesId) {
        Promise<Course> promise = Promise.promise();

        final String FIND_BY_ID_SQL = "SELECT * FROM course WHERE id = $1";

        client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(coursesId))
                .onSuccess(rows -> {
                    if (rows.iterator().hasNext()) {
                        Course course = map(rows.iterator().next());
                        promise.complete(course);
                    } else {
                        promise.fail("Course with id " + coursesId + " does not exist");
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<List<Course>> findByTeacherId(Long teacherId) {
        Promise<List<Course>> promise = Promise.promise();

        final String FIND_BY_TEACHER_SQL = "SELECT * FROM course WHERE teacher_id = $1";

        client.preparedQuery(FIND_BY_TEACHER_SQL)
                .execute(Tuple.of(teacherId))
                .onSuccess(rows -> {
                    List<Course> courses = new ArrayList<>();
                    for (Row row : rows) {
                        Course course = map(row);
                        courses.add(course);
                    }
                    promise.complete(courses);
                })
                .onFailure(promise::fail);

        return promise.future();
    }
}
