package ru.ersted.service;

import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.ersted.config.TransactionalOperator;
import ru.ersted.exception.NotFoundException;
import ru.ersted.mapper.CourseMapper;
import ru.ersted.model.Course;
import ru.ersted.model.Student;
import ru.ersted.module_2vertx.dto.generated.CourseShortDto;
import ru.ersted.module_2vertx.dto.generated.StudentDto;
import ru.ersted.repository.CourseRepository;
import ru.ersted.repository.EnrollmentRepository;
import ru.ersted.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final TransactionalOperator transactionalOperator;

    @Setter
    private StudentRepository studentRepository;

    @Setter
    private CourseRepository courseRepository;

    @Setter
    private CourseEnrichmentService courseEnrichmentService;

    @Setter
    private StudentService studentService;


    public Future<StudentDto> enrollStudentToCourse(Long studentId, Long courseId) {
        return transactionalOperator.transaction(connection ->
                        courseRepository.findById(connection, courseId).compose(optionalCourse -> {

                            if (optionalCourse.isEmpty()) {

                                return Future.failedFuture(new NotFoundException("Course with id '%s' not found"
                                        .formatted(courseId)));
                            }

                            return studentRepository.findById(connection, studentId).compose(optionalStudent -> {

                                if (optionalStudent.isEmpty()) {
                                    return Future.failedFuture(new NotFoundException("Student with id '%s' not found"
                                            .formatted(studentId)));
                                }

                                return enrollmentRepository.enrollStudentToCourse(connection, studentId, courseId);

                            });

                        }))
                .compose(isEnrolled -> studentService.findById(studentId));
    }

    public Future<List<CourseShortDto>> findCoursesByStudentId(Long studentId) {

        return findStudentsCourses(studentId).compose(courses -> {
            List<Future<Course>> enrichedFutures = courses.stream()
                    .map(courseEnrichmentService::enrichWithTeacher)
                    .toList();

            return Future.join(enrichedFutures)
                    .map(joined -> enrichedFutures.stream()
                            .map(Future::result)
                            .map(CourseMapper.INSTANCE::mapToShortDto)
                            .collect(Collectors.toList())
                    );
        });
    }

    public Future<List<Course>> findStudentsCourses(Long studentId) {
        return enrollmentRepository.getCoursesByStudentId(studentId);
    }

    public Future<List<Student>> findStudentsByCourseId(Long courseId) {
        return enrollmentRepository.getStudentsByCourseId(courseId);
    }

}
