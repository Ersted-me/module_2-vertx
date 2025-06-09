package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.module_2vertx.dto.generated.DepartmentCreateRq;
import ru.ersted.service.DepartmentService;
import ru.ersted.util.ApiResponse;

@RequiredArgsConstructor
public class DepartmentHandler {

    private final DepartmentService departmentService;


    public void create(RoutingContext context) {
        DepartmentCreateRq rq = context.body().asJsonObject().mapTo(DepartmentCreateRq.class);

        departmentService.create(rq)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.CREATED, element))
                .onFailure(context::fail);

    }

    public void assigningHeadOfDepartment(RoutingContext context) {
        Long departmentId = Long.parseLong(context.pathParam("departmentId"));
        Long teacherId = Long.parseLong(context.pathParam("teacherId"));

        departmentService.assigningHeadOfDepartment(departmentId, teacherId)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.CREATED, element))
                .onFailure(context::fail);
    }

}
