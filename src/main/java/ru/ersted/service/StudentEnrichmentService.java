package ru.ersted.service;

import io.vertx.core.Future;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ersted.model.Course;
import ru.ersted.model.Student;

import java.util.HashSet;
import java.util.List;

@Setter
@NoArgsConstructor
public class StudentEnrichmentService {

    private EnrollmentService enrollmentService;

    private CourseEnrichmentService courseEnrichmentService;

    public Future<Student> enrichWithCourses(Student student) {
        return enrollmentService.findStudentsCourses(student.getId())
                .compose(courses -> {
                    List<Future<Course>> enrichedCourses = courses.stream()
                            .map(courseEnrichmentService::enrichWithTeacher)
                            .toList();

                    return Future.join(enrichedCourses)
                            .map(joined -> {
                                List<Course> resultCourses = enrichedCourses.stream()
                                        .map(Future::result)
                                        .toList();

                                student.setCourses(new HashSet<>(resultCourses));
                                return student;
                            });
                });
    }

}
