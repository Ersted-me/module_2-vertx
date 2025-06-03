package ru.ersted.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ersted.model.Department;
import ru.ersted.module_2vertx.dto.generated.DepartmentCreateRq;
import ru.ersted.module_2vertx.dto.generated.DepartmentDto;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    Department mapToEntity(DepartmentCreateRq request);

    DepartmentDto mapToDto(Department department);

    List<DepartmentDto> mapToDto(List<Department> departments);

}
