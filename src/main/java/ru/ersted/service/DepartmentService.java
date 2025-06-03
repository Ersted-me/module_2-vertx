package ru.ersted.service;

import io.vertx.core.Future;
import ru.ersted.mapper.DepartmentMapper;
import ru.ersted.model.Department;
import ru.ersted.module_2vertx.dto.generated.DepartmentCreateRq;
import ru.ersted.module_2vertx.dto.generated.DepartmentDto;
import ru.ersted.repository.DepartmentRepository;

public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Future<DepartmentDto> create(DepartmentCreateRq request) {
        Department newDepartment = DepartmentMapper.INSTANCE.mapToEntity(request);

        return departmentRepository.save(newDepartment)
                .map(DepartmentMapper.INSTANCE::mapToDto);
    }


    public Future<DepartmentDto> assigningHeadOfDepartment(Long departmentId, Long teacherId) {
        return departmentRepository.assigningHeadOfDepartment(departmentId, teacherId)
                .compose(unused -> findById(departmentId)
                        .map(DepartmentMapper.INSTANCE::mapToDto)
                );
    }

    public Future<Department> findById(Long departmentId) {
        return departmentRepository.findById(departmentId);
    }

}
