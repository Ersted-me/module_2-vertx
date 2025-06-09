package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Student;

import static ru.ersted.repository.constant.StudentColumn.*;

public class StudentRowMapper extends AbstractRowMapper<Student> {

    @Override
    public Student map(Row row) {

        return Student.builder()
                .id(safe(() -> row.getLong(ID)))
                .name(safe(() -> row.getString(NAME)))
                .email(safe(() -> row.getString(EMAIL)))
                .build();
    }

}
