package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Teacher;

import static ru.ersted.repository.constant.TeacherColumn.*;

public class TeacherRowMapper extends AbstractRowMapper<Teacher> {

    @Override
    public Teacher map(Row row) {

        return Teacher.builder()
                .id(safe(() -> row.getLong(ID)))
                .name(safe(() -> row.getString(NAME)))
                .departmentId(safe(() -> row.getLong(DEPARTMENT_ID)))
                .build();
    }

}
