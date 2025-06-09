package ru.ersted.repository.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DepartmentQueryProvider {

    public static final String SAVE_DEPARTMENT_SQL =
            "INSERT INTO department (name) VALUES ($1) RETURNING id";

    public static final String ASSIGN_HEAD_OF_DEPARTMENT_SQL =
            "UPDATE department SET head_of_department_id = $1 WHERE id = $2";

    public static final String FIND_BY_ID_SQL =
            "SELECT * FROM department WHERE id = $1";

}
