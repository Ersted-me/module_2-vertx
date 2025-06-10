package ru.ersted.service;

import io.vertx.core.Future;
import lombok.Setter;
import ru.ersted.exception.NotFoundException;
import ru.ersted.mapper.TeacherMapper;
import ru.ersted.model.Teacher;
import ru.ersted.module_2vertx.dto.generated.TeacherCreateRq;
import ru.ersted.module_2vertx.dto.generated.TeacherDto;
import ru.ersted.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherService {

    private final TeacherRepository teacherRepository;

    @Setter
    private TeacherEnrichmentService teacherEnrichmentService;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public Future<TeacherDto> create(TeacherCreateRq request) {
        Teacher newTeacher = TeacherMapper.INSTANCE.mapToEntity(request);

        return teacherRepository.save(newTeacher)
                .map(TeacherMapper.INSTANCE::mapToDto);
    }

    public Future<List<TeacherDto>> getAll(int limit, int offset) {

        return teacherRepository.getAll(limit, offset)
                .compose(teachers -> {
                    List<Future<Teacher>> enrichedFutures = teachers.stream()
                            .map(teacherEnrichmentService::enrich) // обогащаем курсами и департаментом
                            .toList();

                    return Future.join(enrichedFutures)
                            .map(joined -> enrichedFutures.stream()
                                    .map(Future::result)
                                    .map(TeacherMapper.INSTANCE::mapToDto)
                                    .collect(Collectors.toList()));
                });
    }

    public Future<Teacher> findById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .compose(optionalTeacher -> optionalTeacher
                        .map(Future::succeededFuture)
                        .orElseGet(() -> Future.failedFuture(
                                new NotFoundException("Teacher with id '%d' not found".formatted(teacherId)))
                        )
                );
    }

}
