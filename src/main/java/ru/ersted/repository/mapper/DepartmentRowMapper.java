package ru.ersted.repository.mapper;

import io.vertx.sqlclient.Row;
import ru.ersted.model.Department;

import static ru.ersted.repository.constant.DepartmentColumn.*;


public class DepartmentRowMapper extends AbstractRowMapper<Department> {

    @Override
    public Department map(Row row) {

        return Department.builder()
                .id(safe(() -> row.getLong(ID)))
                .name(safe(() -> row.getString(NAME)))
                .headOfDepartmentId(safe(() -> row.getLong(HEAD_OF_DEPARTMENT_ID)))
                .build();
    }

}
