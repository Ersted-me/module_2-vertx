package ru.ersted.repository.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeacherQueryProvider {

    public static final String FIND_BY_ID_SQL = "SELECT * FROM teacher WHERE id = $1";

    public static final String GET_ALL_TEACHER_SQL = "SELECT * FROM teacher LIMIT $1 OFFSET $2";

    public static final String SAVE_TEACHER_SQL = "INSERT INTO teacher (name) VALUES ($1) RETURNING id";

}
