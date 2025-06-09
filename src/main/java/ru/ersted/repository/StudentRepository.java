package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.exception.NotFoundException;
import ru.ersted.model.Student;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowMapperUtils;
import ru.ersted.util.RowSetUtils;

import java.util.List;

import static ru.ersted.repository.constant.StudentColumn.ID;
import static ru.ersted.repository.query.StudentQueryProvider.*;

@RequiredArgsConstructor
public class StudentRepository {

    private final Pool client;

    private final RowMapper<Student> rowMapper;


    public Future<Student> save(Student entity) {

        return client.preparedQuery(SAVE_SQL)
                .execute(Tuple.of(entity.getName(), entity.getEmail()))
                .map(rows -> {
                    entity.setId(rows.iterator().next().getLong(ID));
                    return entity;
                });
    }

    public Future<Student> findById(Long id) {

        return client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(id))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper)
                        .orElseThrow(() -> new NotFoundException("Student with id '%s' not found".formatted(id)))
                );
    }

    public Future<List<Student>> getAll(int limit, int offset) {

        return client.preparedQuery(GET_ALL_SQL)
                .execute(Tuple.of(limit, offset))
                .map(rows -> RowMapperUtils.toList(rows, rowMapper));
    }

    public Future<Student> update(Long id, Student student) {

        return client.preparedQuery(UPDATE_SQL)
                .execute(Tuple.of(student.getName(), student.getEmail(), id))
                .map(rows -> {
                    if (RowSetUtils.noRowsAffected(rows)) {
                        throw new NotFoundException("Student with id '%s' not found".formatted(id));
                    } else {
                        student.setId(id);
                        return student;
                    }
                });
    }

    public Future<Void> delete(Long id) {

        return client.preparedQuery(DELETE_SQL)
                .execute(Tuple.of(id))
                .map(rows -> RowSetUtils.requireRowsAffected(rows,
                        () -> new NotFoundException("Student with id '%s' not found".formatted(id))
                ));
    }

}
