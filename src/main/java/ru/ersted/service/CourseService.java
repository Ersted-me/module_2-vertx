package ru.ersted.service;

import io.vertx.core.Future;
import ru.ersted.mapper.CourseMapper;
import ru.ersted.model.Course;
import ru.ersted.model.Teacher;
import ru.ersted.module_2vertx.dto.generated.CourseCreateRq;
import ru.ersted.module_2vertx.dto.generated.CourseDto;
import ru.ersted.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;


public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseEnrichmentService courseEnrichmentService;

    public CourseService(CourseRepository courseRepository, CourseEnrichmentService courseEnrichmentService) {
        this.courseRepository = courseRepository;
        this.courseEnrichmentService = courseEnrichmentService;
    }

    public Future<CourseDto> create(CourseCreateRq courseRq) {
        Course newCourse = CourseMapper.INSTANCE.mapToEntity(courseRq);
        return courseRepository.save(newCourse)
                .map(CourseMapper.INSTANCE::mapToDto);
    }

    public Future<List<CourseDto>> getAll(int limit, int offset) {
        return courseRepository.getAll(limit, offset)
                .compose(courses -> {
                    List<Future<Course>> enrichedCourses = courses.stream()
                            .map(courseEnrichmentService::enrich)
                            .collect(Collectors.toList());

                    return Future.join(enrichedCourses)
                            .map(composite ->
                                    enrichedCourses.stream()
                                            .map(Future::result)
                                            .map(CourseMapper.INSTANCE::mapToDto)
                                            .collect(Collectors.toList())
                            ).onFailure(throwable -> {
                                System.out.println(throwable.getMessage());
                            });
                });
    }

    public Future<CourseDto> assigningTeacher(Long coursesId, Long teacherId) {
        return courseRepository.assigningTeacher(coursesId, teacherId)
                .compose(nothing -> findById(coursesId));
    }

    private Future<CourseDto> findById(Long coursesId) {
        return courseRepository.findById(coursesId)
                .compose(courseEnrichmentService::enrich)
                .map(CourseMapper.INSTANCE::mapToDto);
    }

    public Future<List<Course>> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
}
