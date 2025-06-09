package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.pgclient.PgException;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.exception.DuplicateException;
import ru.ersted.exception.NotFoundException;
import ru.ersted.model.Course;
import ru.ersted.model.Student;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowMapperUtils;
import ru.ersted.util.RowSetUtils;

import java.util.List;

import static ru.ersted.repository.query.EnrollmentQueryProvider.*;


@RequiredArgsConstructor
public class EnrollmentRepository {

    private final static String FOREIGN_KEY_VIOLATION_CODE = "23503";

    private final Pool client;

    private final RowMapper<Course> courseRowMapper;

    private final RowMapper<Student> studentRowMapper;


    public Future<Void> enrollStudentToCourse(Long studentId, Long courseId) {

        return client.preparedQuery(ENROLL_STUDENT_TO_COURSE_SQL)
                .execute(Tuple.of(studentId, courseId))
                .compose(rows -> RowSetUtils.noRowsAffected(rows)
                                ? Future.failedFuture(
                                new DuplicateException(
                                        "Pair Student/Course with id '%s' and '%s' already exists"
                                                .formatted(studentId, courseId)))
                                : Future.succeededFuture(),
                        err -> isForeignKeyViolation(err)
                                ? Future.failedFuture(new NotFoundException(
                                "Student or Course with id '%s' and '%s' not found"
                                        .formatted(studentId, courseId)))
                                : Future.failedFuture(err)
                );
    }

    public Future<List<Course>> getCoursesByStudentId(Long studentId) {
        return client.preparedQuery(COURSES_BY_STUDENT_ID_SQL)
                .execute(Tuple.of(studentId))
                .map(rows -> RowMapperUtils.toList(rows, courseRowMapper));
    }

    public Future<List<Student>> getStudentsByCourseId(Long courseId) {

        return client.preparedQuery(STUDENTS_BY_COURSE_ID)
                .execute(Tuple.of(courseId))
                .map(rows -> RowMapperUtils.toList(rows, studentRowMapper));
    }

    private boolean isForeignKeyViolation(Throwable t) {
        return t instanceof PgException pg && FOREIGN_KEY_VIOLATION_CODE.equals(pg.getSqlState());
    }

}
