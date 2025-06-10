package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.model.Department;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowSetUtils;

import java.util.Optional;

import static ru.ersted.repository.constant.DepartmentColumn.ID;
import static ru.ersted.repository.query.DepartmentQueryProvider.*;

@RequiredArgsConstructor
public class DepartmentRepository {

    private final Pool client;

    private final RowMapper<Department> rowMapper;


    public Future<Department> save(Department entity) {
        return client.preparedQuery(SAVE_DEPARTMENT_SQL)
                .execute(Tuple.of(entity.getName()))
                .map(rows -> {
                    entity.setId(rows.iterator().next().getLong(ID));
                    return entity;
                });
    }

    public Future<Optional<Department>> assigningHeadOfDepartment(SqlConnection connection, Long departmentId, Long teacherId) {

        return connection.preparedQuery(ASSIGN_HEAD_OF_DEPARTMENT_SQL)
                .execute(Tuple.of(teacherId, departmentId))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper));
    }

    public Future<Optional<Department>> findById(Long departmentId) {

        return client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(departmentId))
                .map(rows -> RowSetUtils.firstRow(rows, rowMapper));
    }

}
