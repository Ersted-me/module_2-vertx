package ru.ersted.service;

import io.vertx.core.Future;
import lombok.Setter;
import ru.ersted.mapper.StudentMapper;
import ru.ersted.model.Student;
import ru.ersted.repository.StudentRepository;
import ru.ersted.module_2vertx.dto.generated.StudentDto;
import ru.ersted.module_2vertx.dto.generated.StudentCreateRq;
import ru.ersted.module_2vertx.dto.generated.StudentUpdateRq;

import java.util.List;
import java.util.stream.Collectors;

public class StudentService {

    private final StudentRepository studentRepository;

    @Setter
    private StudentEnrichmentService studentEnrichmentService;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public Future<StudentDto> create(StudentCreateRq request) {
        Student newStudent = StudentMapper.INSTANCE.mapToEntity(request);

        return studentRepository.save(newStudent)
                .map(StudentMapper.INSTANCE::mapToDto);
    }

    public Future<List<StudentDto>> getAll(int limit, int offset) {
        return studentRepository.getAll(limit, offset)
                .compose(students -> {
                    List<Future<Student>> enrichedFutures = students.stream()
                            .map(studentEnrichmentService::enrichWithCourses)
                            .toList();

                    return Future.join(enrichedFutures)
                            .map(joined -> enrichedFutures.stream()
                                    .map(Future::result)
                                    .map(StudentMapper.INSTANCE::mapToDto)
                                    .collect(Collectors.toList()));
                });
    }

    public Future<StudentDto> findById(Long studentId) {
        return studentRepository.findById(studentId)
                .compose(studentEnrichmentService::enrichWithCourses)
                .map(StudentMapper.INSTANCE::mapToDto);
    }


    public Future<StudentDto> update(Long id, StudentUpdateRq rq) {
        Student student = StudentMapper.INSTANCE.mapToEntity(rq);

        return studentRepository.update(id, student)
                .compose(studentEnrichmentService::enrichWithCourses)
                .map(StudentMapper.INSTANCE::mapToDto);
    }

    public Future<Void> delete(Long id) {
        return studentRepository.delete(id);
    }

}
