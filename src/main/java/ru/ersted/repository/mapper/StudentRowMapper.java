package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Student;

public class StudentRowMapper {
    private StudentRowMapper() {}

    public static Student map(Row row) {
        Student.StudentBuilder studentBuilder = Student.builder();

        studentBuilder.id(row.getLong("id"));
        studentBuilder.name(row.getString("name"));
        studentBuilder.email(row.getString("email"));

        return studentBuilder.build();
    }

}
