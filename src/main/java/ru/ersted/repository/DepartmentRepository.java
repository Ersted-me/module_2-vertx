package ru.ersted.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import ru.ersted.model.Department;
import ru.ersted.repository.mapper.DepartmentRowMapper;

public class DepartmentRepository {

    private final Pool client;

    public DepartmentRepository(Pool databaseClient) {
        this.client = databaseClient;
    }

    public Future<Department> save(Department entity) {
        Promise<Department> promise = Promise.promise();

        final String SAVE_DEPARTMENT_SQL = "INSERT INTO department (name) VALUES ($1) RETURNING id;";

        client.preparedQuery(SAVE_DEPARTMENT_SQL)
                .execute(Tuple.of(entity.getName()))
                .onSuccess(rows -> {
                    if (rows.iterator().hasNext()) {
                        entity.setId(rows.iterator().next().getLong("id"));
                        promise.complete(entity);
                    } else {
                        //todo разобраться со всеми ошибками в репозиториях
                        promise.fail(new Exception("Unable to save department"));
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Void> assigningHeadOfDepartment(Long departmentId, Long teacherId) {
        Promise<Void> promise = Promise.promise();

        final String ASSIGN_HEAD_OF_DEPARTMENT_SQL = "UPDATE department SET head_of_department_id = $1 WHERE id = $2;";

        client.preparedQuery(ASSIGN_HEAD_OF_DEPARTMENT_SQL)
                .execute(Tuple.of(teacherId, departmentId))
                .onSuccess(rows -> {
                    if (rows.rowCount() == 0) {
                        promise.fail(new Exception("Unable to assign head of department"));
                    } else {
                        promise.complete();
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

    public Future<Department> findById(Long departmentId) {
        Promise<Department> promise = Promise.promise();

        final String FIND_BY_ID_SQL = "SELECT * FROM department WHERE id = $1;";

        client.preparedQuery(FIND_BY_ID_SQL)
                .execute(Tuple.of(departmentId))
                .onSuccess(rows -> {
                    if (rows.iterator().hasNext()) {
                        Department department = DepartmentRowMapper.map(rows.iterator().next());
                        promise.complete(department);
                    } else {
                        promise.fail(new Exception("Unable to find department"));
                    }
                })
                .onFailure(promise::fail);

        return promise.future();
    }

}
