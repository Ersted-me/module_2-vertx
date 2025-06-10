package ru.ersted.service;

import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;
import ru.ersted.config.TransactionalOperator;
import ru.ersted.exception.NotFoundException;
import ru.ersted.mapper.DepartmentMapper;
import ru.ersted.model.Department;
import ru.ersted.module_2vertx.dto.generated.DepartmentCreateRq;
import ru.ersted.module_2vertx.dto.generated.DepartmentDto;
import ru.ersted.repository.DepartmentRepository;
import ru.ersted.repository.TeacherRepository;

import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final TeacherRepository teacherRepository;

    private final TransactionalOperator transactionalOperator;


    private static Function<Optional<Department>, Future<Department>> departmentOrNotFound(Long departmentId) {
        return optionalDepartment -> optionalDepartment
                .map(Future::succeededFuture)
                .orElseGet(() -> Future.failedFuture(
                        new NotFoundException("Department with id '%d' not found".formatted(departmentId)))
                );
    }


    public Future<DepartmentDto> create(DepartmentCreateRq request) {
        Department newDepartment = DepartmentMapper.INSTANCE.mapToEntity(request);

        return departmentRepository.save(newDepartment)
                .map(DepartmentMapper.INSTANCE::mapToDto);
    }


    public Future<DepartmentDto> assigningHeadOfDepartment(Long departmentId, Long teacherId) {

        return transactionalOperator.transaction(connection ->
                        teacherRepository.findById(connection, teacherId).compose(optionalTeacher -> {

                            if (optionalTeacher.isEmpty()) {
                                return Future.failedFuture(
                                        new NotFoundException("Teacher with id '%d' not found".formatted(teacherId)));
                            }

                            return departmentRepository.assigningHeadOfDepartment(connection, departmentId, teacherId)
                                    .compose(departmentOrNotFound(departmentId));

                        }))
                .map(DepartmentMapper.INSTANCE::mapToDto);
    }

    public Future<Department> findById(Long departmentId) {

        return departmentRepository.findById(departmentId)
                .compose(departmentOrNotFound(departmentId));
    }

}
