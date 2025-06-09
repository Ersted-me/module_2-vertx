package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import ru.ersted.exception.NotFoundException;
import ru.ersted.model.Department;
import ru.ersted.repository.mapper.RowMapper;
import ru.ersted.util.RowSetUtils;

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

    public Future<Void> assigningHeadOfDepartment(Long departmentId, Long teacherId) {

        return client.preparedQuery(ASSIGN_HEAD_OF_DEPARTMENT_SQL)
                .execute(Tuple.of(teacherId, departmentId))
                .compose(rows -> RowSetUtils.noRowsAffected(rows)
                        ? Future.succeededFuture()
                        : Future.failedFuture(new NotFoundException("Department with id '%s' not found"))
                );
    }

    public Future<Department> findById(Long departmentId) {

        return client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(departmentId))
                .compose(rows -> rows.iterator().hasNext()
                        ? Future.succeededFuture(rowMapper.map(rows.iterator().next()))
                        : Future.failedFuture(new NotFoundException("Department with id '%s' not found"))
                );
    }

}
