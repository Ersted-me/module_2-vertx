package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import ru.ersted.model.Course;
import ru.ersted.model.Student;
import ru.ersted.repository.mapper.StudentRowMapper;

import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    private final Pool client;

    public StudentRepository(Pool databaseClient) {
        this.client = databaseClient;
    }

    public Future<Student> save(Student entity) {
        final String SAVE_SQL = "INSERT INTO student(name, email) VALUES($1, $2) RETURNING id";

        return client.preparedQuery(SAVE_SQL)
                .execute(Tuple.of(entity.getName(), entity.getEmail()))
                .map(rows -> {
                    entity.setId(rows.iterator().next().getLong("id"));
                    return entity;
                });
    }

    public Future<Student> findById(Long id) {
        Promise<Student> promise = Promise.promise();

        final String FIND_BY_ID_SQL = "SELECT * FROM student WHERE id = $1";

        client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(id))
                .onSuccess(rows -> {
                    if (rows.iterator().hasNext()) {
                        Student student = StudentRowMapper.map(rows.iterator().next());
                        promise.complete(student);
                    } else {
                        //todo добавить ошибку для NotFound статуса
                        promise.fail("Student with id " + id + " not found");
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<List<Student>> getAll(int limit, int offset) {
        Promise<List<Student>> promise = Promise.promise();

        final String GET_ALL_SQL = "SELECT * FROM student LIMIT $1 OFFSET $2";

        client.preparedQuery(GET_ALL_SQL)
                .execute(Tuple.of(limit, offset))
                .onSuccess(rows -> {
                    List<Student> students = new ArrayList<>();
                    for (Row row : rows) {
                        Student student = StudentRowMapper.map(row);
                        students.add(student);
                    }
                    promise.complete(students);
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Student> update(Long id, Student student) {
        Promise<Student> promise = Promise.promise();

        final String UPDATE_SQL = "UPDATE student SET name = $1, email = $2 WHERE id = $3";

        client.preparedQuery(UPDATE_SQL)
                .execute(Tuple.of(student.getName(), student.getEmail(), id))
                .onSuccess(rows -> {
                    if (rows.rowCount() == 0) {
                        promise.fail("Student with id " + id + " not found");
                    } else {
                        student.setId(id);
                        promise.complete(student);
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Void> delete(Long id) {
        Promise<Void> promise = Promise.promise();

        final String DELETE_SQL = "DELETE FROM student WHERE id = $1";

        client.preparedQuery(DELETE_SQL)
                .execute(Tuple.of(id))
                .onSuccess(rows -> {
                    if (rows.rowCount() == 0) {
                        promise.fail("Student with id " + id + " not found");
                    } else {
                        promise.complete();
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

}
