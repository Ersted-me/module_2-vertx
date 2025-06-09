package ru.ersted.repository.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CourseQueryProvider {

    public static final String INSERT =
            "INSERT INTO course (title, teacher_id) VALUES ($1, $2) RETURNING id";

    public static final String SELECT_ALL =
            "SELECT * FROM course LIMIT $1 OFFSET $2";

    public static final String SELECT_BY_ID =
            "SELECT * FROM course WHERE id = $1";

    public static final String UPDATE_TEACHER_ID =
            "UPDATE course SET teacher_id = $1 WHERE id = $2";

    public static final String FIND_BY_TEACHER_ID =
            "SELECT * FROM course WHERE teacher_id = $1";

}
