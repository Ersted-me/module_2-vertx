package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Department;

public class DepartmentRowMapper {
    private DepartmentRowMapper() {
    }

    public static Department map(Row row) {
        Department.DepartmentBuilder departmentBuilder = Department.builder();

        departmentBuilder.id(row.getLong("id"));
        departmentBuilder.name(row.getString("name"));
        departmentBuilder.headOfDepartmentId(row.getLong("head_of_department_id"));

        return departmentBuilder.build();
    }

}
