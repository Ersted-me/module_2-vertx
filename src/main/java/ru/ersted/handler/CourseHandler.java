package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.module_2vertx.dto.generated.CourseCreateRq;
import ru.ersted.service.CourseService;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.ParserUtils;

@RequiredArgsConstructor
public class CourseHandler {

    private final CourseService courseService;


    public void getAll(RoutingContext context) {
        int limit = ParserUtils.parseIntOrDefault(context.request().getParam("limit"), 10);
        int offset = ParserUtils.parseIntOrDefault(context.request().getParam("offset"), 0);

        courseService.getAll(limit, offset)
                .onSuccess(list -> ApiResponse.send(context, HttpResponseStatus.OK, list))
                .onFailure(context::fail);
    }


    public void create(RoutingContext context) {
        CourseCreateRq courseRq = context.body().asJsonObject().mapTo(CourseCreateRq.class);

        courseService.create(courseRq)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.CREATED, element));
    }

}
