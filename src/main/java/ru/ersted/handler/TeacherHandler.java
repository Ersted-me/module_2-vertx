package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.module_2vertx.dto.generated.TeacherCreateRq;
import ru.ersted.service.CourseService;
import ru.ersted.service.TeacherService;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.ParserUtils;

@RequiredArgsConstructor
public class TeacherHandler {

    private final TeacherService teacherService;

    private final CourseService courseService;


    public void create(RoutingContext context) {
        TeacherCreateRq rq = context.body().asJsonObject().mapTo(TeacherCreateRq.class);

        teacherService.create(rq)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.CREATED, element))
                .onFailure(context::fail);

    }

    public void assigningTeacherToCourse(RoutingContext context) {
        Long teacherId = Long.parseLong(context.pathParam("teacherId"));
        Long coursesId = Long.parseLong(context.pathParam("coursesId"));

        courseService.assigningTeacher(coursesId, teacherId)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.OK, element))
                .onFailure(context::fail);

    }

    public void findAll(RoutingContext context) {
        int limit = ParserUtils.parseIntOrDefault(context.request().getParam("limit"), 10);
        int offset = ParserUtils.parseIntOrDefault(context.request().getParam("offset"), 0);

        teacherService.getAll(limit, offset)
                .onSuccess(list -> ApiResponse.send(context, HttpResponseStatus.OK, list))
                .onFailure(context::fail);
    }

}
