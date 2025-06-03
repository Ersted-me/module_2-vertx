package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import ru.ersted.model.Course;
import ru.ersted.model.Student;
import ru.ersted.repository.mapper.CourseRowMapper;
import ru.ersted.repository.mapper.StudentRowMapper;

import java.util.ArrayList;
import java.util.List;


public class EnrollmentRepository {
    private final Pool client;

    public EnrollmentRepository(Pool client) {
        this.client = client;
    }


    public Future<Void> enrollStudentToCourse(Long studentId, Long courseId) {
        Promise<Void> promise = Promise.promise();

        final String ENROLL_STUDENT_TO_COURSE_SQL = """
                INSERT INTO students_courses(student_id, course_id) VALUES ($1, $2) ON CONFLICT DO NOTHING
                """;

        client.preparedQuery(ENROLL_STUDENT_TO_COURSE_SQL)
                .execute(Tuple.of(studentId, courseId))
                .onSuccess(rows -> {
                    if (rows.rowCount() == 0) {
                        promise.fail("No students enrolled");
                    } else {
                        promise.complete();
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<List<Course>> getCoursesByStudentId(Long studentId) {
        Promise<List<Course>> promise = Promise.promise();

        final String COURSES_BY_STUDENT_ID_SQL = """
                SELECT c.* FROM course c 
                JOIN students_courses sc ON c.id = sc.course_id
                WHERE sc.student_id = $1 
                """;

        client.preparedQuery(COURSES_BY_STUDENT_ID_SQL)
                .execute(Tuple.of(studentId))
                .onSuccess(rows -> {
                    List<Course> courses = new ArrayList<>();

                    for (Row row : rows) {
                        Course course = CourseRowMapper.map(row);
                        courses.add(course);
                    }

                    promise.complete(courses);
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<List<Student>> getStudentsByCourseId(Long courseId) {
        Promise<List<Student>> promise = Promise.promise();

        final String STUDENTS_BY_COURSE_ID = """
                SELECT s.* FROM student s 
                JOIN students_courses sc ON s.id = sc.student_id
                WHERE sc.course_id = $1
                """;

        client.preparedQuery(STUDENTS_BY_COURSE_ID)
                .execute(Tuple.of(courseId))
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
}
