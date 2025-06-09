package ru.ersted.config.di.builder;

import ru.ersted.repository.mapper.CourseRowMapper;
import ru.ersted.repository.mapper.DepartmentRowMapper;
import ru.ersted.repository.mapper.StudentRowMapper;
import ru.ersted.repository.mapper.TeacherRowMapper;

public record RowMappers(
        CourseRowMapper courseRowMapper,
        StudentRowMapper studentRowMapper,
        DepartmentRowMapper departmentRowMapper,
        TeacherRowMapper teacherRowMapper) {
}
