package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Teacher;

public class TeacherRowMapper {

    private TeacherRowMapper() {}

    public static Teacher map(Row row) {
        Teacher.TeacherBuilder teacherBuilder = Teacher.builder();

        teacherBuilder.id(row.getLong("id"));
        teacherBuilder.name(row.getString("name"));
        teacherBuilder.departmentId(row.getLong("department_id"));

        return teacherBuilder.build();
    }

}
