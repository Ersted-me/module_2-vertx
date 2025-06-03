package ru.ersted.service;

import io.vertx.core.Future;
import lombok.Setter;
import ru.ersted.mapper.CourseMapper;
import ru.ersted.model.Course;
import ru.ersted.model.Student;
import ru.ersted.module_2vertx.dto.generated.CourseShortDto;
import ru.ersted.module_2vertx.dto.generated.StudentDto;
import ru.ersted.repository.EnrollmentRepository;

import java.util.List;
import java.util.stream.Collectors;


public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Setter
    private StudentService studentService;

    @Setter
    private CourseEnrichmentService courseEnrichmentService;


    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public Future<StudentDto> enrollStudentToCourse(Long studentId, Long courseId) {
        return enrollmentRepository.enrollStudentToCourse(studentId, courseId)
                .compose(future -> studentService.findById(studentId));
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
