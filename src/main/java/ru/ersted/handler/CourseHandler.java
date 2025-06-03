package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import ru.ersted.module_2vertx.dto.generated.CourseCreateRq;
import ru.ersted.service.CourseService;
import ru.ersted.util.ParserUtil;

public class CourseHandler {
    private final CourseService courseService;

    public CourseHandler(CourseService courseService) {
        this.courseService = courseService;
    }

    public void getAll(RoutingContext context) {
        int limit = ParserUtil.parseIntOrDefault(context.request().getParam("limit"), 10);
        int offset = ParserUtil.parseIntOrDefault(context.request().getParam("offset"), 0);

        courseService.getAll(limit, offset)
                .onSuccess(dtos ->
                        context.response()
                                .setStatusCode(HttpResponseStatus.OK.code())
                                .putHeader("content-type", "application/json")
                                .end(Json.encode(dtos)))
                .onFailure(error -> context.response()
                        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                        .putHeader("content-type", "application/json")
                        .end(JsonObject.mapFrom(error).encode()));
    }


    public void create(RoutingContext context) {
        JsonObject json = context.body().asJsonObject();
        CourseCreateRq courseRq = json.mapTo(CourseCreateRq.class);

        courseService.create(courseRq)
                .onSuccess(dto -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.CREATED.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(dto).encode());
                });
    }

}
