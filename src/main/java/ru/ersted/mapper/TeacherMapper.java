package ru.ersted.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ersted.model.Teacher;
import ru.ersted.module_2vertx.dto.generated.TeacherCreateRq;
import ru.ersted.module_2vertx.dto.generated.TeacherShortDto;
import ru.ersted.module_2vertx.dto.generated.TeacherDto;

import java.util.List;

@Mapper
public interface TeacherMapper {
    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);

    Teacher mapToEntity(TeacherCreateRq request);

    TeacherDto mapToDto(Teacher teacher);

    List<TeacherDto> mapToDto(List<Teacher> teachers);

    TeacherShortDto toShortDto(Teacher teacher);

}
