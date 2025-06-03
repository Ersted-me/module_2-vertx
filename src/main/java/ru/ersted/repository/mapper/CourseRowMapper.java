package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Course;

public class CourseRowMapper {

    private CourseRowMapper() {
    }

    public static Course map(Row row) {
        Course.CourseBuilder courseBuilder = Course.builder();

        courseBuilder.id(row.getLong("id"));
        courseBuilder.title(row.getString("title"));
        courseBuilder.teacherId(row.getLong("teacher_id"));

        return courseBuilder.build();
    }

}
