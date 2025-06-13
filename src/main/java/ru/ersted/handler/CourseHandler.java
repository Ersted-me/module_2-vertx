package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.Paging;
import ru.ersted.util.RequestUtil;

import static ru.ersted.constant.EventBusAddress.COURSE_CREATE;
import static ru.ersted.constant.EventBusAddress.COURSE_GET_ALL;
import static ru.ersted.exception.util.ExceptionUtil.processException;

@RequiredArgsConstructor
public class CourseHandler {

    public void getAll(RoutingContext ctx, Vertx vertx) {

        Paging paging = Paging.fromQuery(ctx);

        JsonObject request = paging.toJson();

        vertx.eventBus().<JsonObject>request(COURSE_GET_ALL.name(), request)
                .onSuccess(resp -> ApiResponse.send(ctx, HttpResponseStatus.OK, resp.body()))
                .onFailure(processException(ctx));

    }

    public void create(RoutingContext ctx, Vertx vertx) {

        JsonObject request = RequestUtil.requireJsonBody(ctx);

        vertx.eventBus().<JsonObject>request(COURSE_CREATE.name(), request)
                .onSuccess(resp -> ApiResponse.send(ctx, HttpResponseStatus.CREATED, resp.body()))
                .onFailure(processException(ctx));
    }

}
