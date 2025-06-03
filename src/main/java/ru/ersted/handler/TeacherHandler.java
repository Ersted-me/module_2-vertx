package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Setter;
import ru.ersted.module_2vertx.dto.generated.TeacherCreateRq;
import ru.ersted.service.CourseService;
import ru.ersted.service.TeacherService;
import ru.ersted.util.ParserUtil;

public class TeacherHandler {

    private final TeacherService teacherService;

    @Setter
    private CourseService courseService;

    public TeacherHandler(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public void create(RoutingContext context) {
        JsonObject json = context.body().asJsonObject();
        TeacherCreateRq rq = json.mapTo(TeacherCreateRq.class);

        teacherService.create(rq)
                .onSuccess(teacher -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(teacher).encode());
                });

    }

    public void assigningTeacherToCourse(RoutingContext context) {
        Long teacherId = Long.parseLong(context.pathParam("teacherId"));
        Long coursesId = Long.parseLong(context.pathParam("coursesId"));

        courseService.assigningTeacher(coursesId, teacherId)
                .onSuccess(course -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(course).encode());
                });

    }

    public void findAll(RoutingContext context) {
        int limit = ParserUtil.parseIntOrDefault(context.request().getParam("limit"), 10);
        int offset = ParserUtil.parseIntOrDefault(context.request().getParam("offset"), 0);

        teacherService.getAll(limit, offset)
                .onSuccess(teachers -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(teachers).encode());
                });
    }

}
