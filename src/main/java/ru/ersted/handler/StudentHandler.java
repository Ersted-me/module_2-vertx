package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.Paging;
import ru.ersted.util.ParserUtils;
import ru.ersted.util.RequestUtil;

import static ru.ersted.constant.EventBusAddress.*;
import static ru.ersted.exception.util.ExceptionUtil.processException;

@RequiredArgsConstructor
public class StudentHandler {


    public void create(RoutingContext context, Vertx vertx) {

        JsonObject request = RequestUtil.requireJsonBody(context);

        vertx.eventBus().<JsonObject>request(STUDENT_CREATE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.CREATED, resp.body()))
                .onFailure(processException(context));

    }

    public void getAll(RoutingContext context, Vertx vertx) {

        Paging paging = Paging.fromQuery(context);

        JsonObject request = paging.toJson();

        vertx.eventBus().<JsonObject>request(STUDENT_GET_ALL.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

    public void findById(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();

        request.put("id", context.pathParam("id"));

        vertx.eventBus().<JsonObject>request(STUDENT_FIND_BY_ID.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

    public void update(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();

        request.put("body", RequestUtil.requireJsonBody(context));
        request.put("id", context.pathParam("id"));

        vertx.eventBus().<JsonObject>request(STUDENT_UPDATE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

    public void delete(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();

        request.put("id", ParserUtils.toLong(context.pathParam("id")));

        vertx.eventBus().<JsonObject>request(STUDENT_DELETE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

    public void addCourse(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();

        request.put("courseId", context.pathParam("courseId"));
        request.put("studentId", context.pathParam("studentId"));

        vertx.eventBus().<JsonObject>request(STUDENT_ADD_COURSE.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

    public void findCourses(RoutingContext context, Vertx vertx) {

        JsonObject request = new JsonObject();

        request.put("studentId", context.pathParam("studentId"));

        vertx.eventBus().<JsonObject>request(STUDENT_FIND_COURSES.name(), request)
                .onSuccess(resp -> ApiResponse.send(context, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(context));

    }

}
