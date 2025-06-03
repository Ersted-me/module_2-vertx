package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import ru.ersted.service.DepartmentService;
import ru.ersted.module_2vertx.dto.generated.DepartmentCreateRq;

public class DepartmentHandler {

    private final DepartmentService departmentService;

    public DepartmentHandler(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void create(RoutingContext context) {
        JsonObject json = context.body().asJsonObject();
        DepartmentCreateRq rq = json.mapTo(DepartmentCreateRq.class);

        departmentService.create(rq)
                .onSuccess(department -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.CREATED.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(department).encode());
                });

    }

    public void assigningHeadOfDepartment(RoutingContext context) {
        Long departmentId = Long.parseLong(context.pathParam("departmentId"));
        Long teacherId = Long.parseLong(context.pathParam("teacherId"));

        departmentService.assigningHeadOfDepartment(departmentId, teacherId)
                .onSuccess(department -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.CREATED.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(department).encode());
                });
    }

}
