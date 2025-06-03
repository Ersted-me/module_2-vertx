package ru.ersted.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ersted.model.Student;
import ru.ersted.module_2vertx.dto.generated.StudentCreateRq;
import ru.ersted.module_2vertx.dto.generated.StudentUpdateRq;
import ru.ersted.module_2vertx.dto.generated.StudentShortDto;
import ru.ersted.module_2vertx.dto.generated.StudentDto;

import java.util.List;

@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    Student mapToEntity(StudentCreateRq request);

    Student mapToEntity(StudentUpdateRq request);

    StudentDto mapToDto(Student student);

    List<StudentDto> mapToDto(List<Student> students);

    StudentShortDto toShortDto(Student student);

}
