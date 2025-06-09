package ru.ersted.repository.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudentQueryProvider {

    public static final String SAVE_SQL = "INSERT INTO student(name, email) VALUES($1, $2) RETURNING id";

    public static final String FIND_BY_ID_SQL = "SELECT * FROM student WHERE id = $1";

    public static final String GET_ALL_SQL = "SELECT * FROM student LIMIT $1 OFFSET $2";

    public static final String UPDATE_SQL = "UPDATE student SET name = $1, email = $2 WHERE id = $3";

    public static final String DELETE_SQL = "DELETE FROM student WHERE id = $1";

}
