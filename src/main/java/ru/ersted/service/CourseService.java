package ru.ersted.service;

import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;
import ru.ersted.config.TransactionalOperator;
import ru.ersted.exception.NotFoundException;
import ru.ersted.mapper.CourseMapper;
import ru.ersted.model.Course;
import ru.ersted.module_2vertx.dto.generated.CourseCreateRq;
import ru.ersted.module_2vertx.dto.generated.CourseDto;
import ru.ersted.repository.CourseRepository;
import ru.ersted.repository.TeacherRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final TeacherRepository teacherRepository;

    private final CourseEnrichmentService courseEnrichmentService;

    private final TransactionalOperator transactionalOperator;


    private static Function<Optional<Course>, Future<Course>> courseOrNotFound(long courseId) {
        return opt -> opt
                .map(Future::succeededFuture)
                .orElseGet(() -> Future.failedFuture(
                        new NotFoundException("Course with id '%d' not found".formatted(courseId)))
                );
    }


    public Future<CourseDto> create(CourseCreateRq courseRq) {
        Course newCourse = CourseMapper.INSTANCE.mapToEntity(courseRq);
        return courseRepository.save(newCourse)
                .map(CourseMapper.INSTANCE::mapToDto);
    }

    public Future<List<CourseDto>> getAll(int limit, int offset) {

        return courseRepository.getAll(limit, offset).compose(courses -> {

            List<Future<Course>> enrichedCourses = courses.stream()
                    .map(courseEnrichmentService::enrich)
                    .collect(Collectors.toList());

            return Future.join(enrichedCourses)
                    .map(composite -> enrichedCourses.stream()
                            .map(Future::result)
                            .map(CourseMapper.INSTANCE::mapToDto)
                            .collect(Collectors.toList()));
        });

    }

    public Future<CourseDto> assigningTeacher(Long coursesId, Long teacherId) {
        return transactionalOperator.transaction(connection ->
                        teacherRepository.findById(connection, teacherId).compose(optionalTeacher -> {

                            if (optionalTeacher.isEmpty()) {
                                return Future.failedFuture(
                                        new NotFoundException("Teacher with id '%d' not found".formatted(teacherId)));
                            }

                            return courseRepository.assigningTeacher(connection, coursesId, teacherId)
                                    .compose(courseOrNotFound(coursesId));
                        })
                )
                .compose(courseEnrichmentService::enrich)
                .map(CourseMapper.INSTANCE::mapToDto);
    }

    public Future<List<Course>> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

}
