package ru.ersted.repository.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnrollmentQueryProvider {

    public static final String ENROLL_STUDENT_TO_COURSE_SQL =
            "INSERT INTO students_courses(student_id, course_id) VALUES ($1, $2) ON CONFLICT DO NOTHING";

    public static final String COURSES_BY_STUDENT_ID_SQL =
            "SELECT c.* FROM course c JOIN students_courses sc ON c.id = sc.course_id WHERE sc.student_id = $1";

    public static final String STUDENTS_BY_COURSE_ID =
            "SELECT s.* FROM student s JOIN students_courses sc ON s.id = sc.student_id WHERE sc.course_id = $1";
}
