package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.Paging;
import ru.ersted.util.RequestUtil;

import static ru.ersted.constant.EventBusAddress.*;
import static ru.ersted.exception.util.ExceptionUtil.processException;

@RequiredArgsConstructor
public class TeacherHandler {

    public void create(RoutingContext context, Vertx vertx) {

        JsonObject request = RequestUtil.requireJsonBody(context);

        vertx.eventBus().<JsonObject>request(TEACHER_CREATE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.CREATED, resp.body()))
                .onFailure(processException(context));

    }

    public void assigningTeacherToCourse(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();

        request.put("teacherId", context.pathParam("teacherId"));
        request.put("coursesId", context.pathParam("coursesId"));

        vertx.eventBus().<JsonObject>request(TEACHER_ASSIGNING_TEACHER_TO_COURSE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

    public void findAll(RoutingContext context, Vertx vertx) {

        Paging paging = Paging.fromQuery(context);

        JsonObject request = paging.toJson();

        vertx.eventBus().<JsonObject>request(TEACHER_GET_ALL.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

}
