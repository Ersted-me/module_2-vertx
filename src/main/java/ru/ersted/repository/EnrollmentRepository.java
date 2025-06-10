package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.model.Course;
import ru.ersted.model.Student;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowMapperUtils;
import ru.ersted.util.RowSetUtils;

import java.util.List;

import static ru.ersted.repository.query.EnrollmentQueryProvider.*;


@RequiredArgsConstructor
public class EnrollmentRepository {

    private final Pool client;

    private final RowMapper<Course> courseRowMapper;

    private final RowMapper<Student> studentRowMapper;


    public Future<Boolean> enrollStudentToCourse(SqlConnection connection, Long studentId, Long courseId) {

        return connection.preparedQuery(ENROLL_STUDENT_TO_COURSE_SQL)
                .execute(Tuple.of(studentId, courseId))
                .map(RowSetUtils::noRowsAffected);
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

}
