package ru.ersted.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.ersted.model.Course;
import ru.ersted.module_2vertx.dto.generated.CourseCreateRq;
import ru.ersted.module_2vertx.dto.generated.CourseShortDto;
import ru.ersted.module_2vertx.dto.generated.CourseDto;

import java.util.List;

@Mapper
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    Course mapToEntity(CourseCreateRq courseCreateRq);

    CourseDto mapToDto(Course course);

    List<CourseDto> mapToDto(List<Course> course);

    CourseShortDto mapToShortDto(Course entity);

    List<CourseShortDto> mapToShortDto(List<Course> entities);

}
