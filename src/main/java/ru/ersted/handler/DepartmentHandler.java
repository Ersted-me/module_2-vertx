package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.RequestUtil;

import static ru.ersted.constant.EventBusAddress.DEPARTMENT_ASSIGNING_HEAD_OF_DEPARTMENT;
import static ru.ersted.constant.EventBusAddress.DEPARTMENT_CREATE;
import static ru.ersted.exception.util.ExceptionUtil.processException;

@RequiredArgsConstructor
public class DepartmentHandler {


    public void create(RoutingContext context, Vertx vertx) {

        JsonObject request = RequestUtil.requireJsonBody(context);

        vertx.eventBus().<JsonObject>request(DEPARTMENT_CREATE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.CREATED, resp.body()))
                .onFailure(processException(context));
    }

    public void assigningHeadOfDepartment(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();
        request.put("departmentId", context.pathParam("departmentId"));
        request.put("teacherId", context.pathParam("teacherId"));

        vertx.eventBus().<JsonObject>request(DEPARTMENT_ASSIGNING_HEAD_OF_DEPARTMENT.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.CREATED, resp.body()))
                .onFailure(processException(context));
    }

}
