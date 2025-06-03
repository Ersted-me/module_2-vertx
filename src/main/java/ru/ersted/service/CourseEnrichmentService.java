package ru.ersted.service;

import io.vertx.core.Future;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ersted.model.Course;

import java.util.HashSet;


@Setter
@NoArgsConstructor
public class CourseEnrichmentService {
    private TeacherService teacherService;

    private EnrollmentService enrollmentService;

    public Future<Course> enrich(Course course) {
        return enrichWithTeacher(course)
                .compose(this::enrichWithStudents);
    }

    public Future<Course> enrichWithTeacher(Course course) {
        return teacherService.findById(course.getTeacherId())
                .map(teacher -> {
                    course.setTeacher(teacher);
                    return course;
                });
    }

    public Future<Course> enrichWithStudents(Course course) {
        return enrollmentService.findStudentsByCourseId(course.getId())
                .map(students -> {
                    course.setStudents(new HashSet<>(students));
                    return course;
                });
    }

}
