package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Course;

import static ru.ersted.repository.constant.CourseColumn.*;

public class CourseRowMapper extends AbstractRowMapper<Course> {

    @Override
    public Course map(Row row) {

        return Course.builder()
                .id(safe(() -> row.getLong(ID)))
                .title(safe(() -> row.getString(TITLE)))
                .teacherId(safe(() -> row.getLong(TEACHER_ID)))
                .build();
    }

}
