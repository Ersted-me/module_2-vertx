package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import ru.ersted.service.EnrollmentService;
import ru.ersted.service.StudentService;
import ru.ersted.module_2vertx.dto.generated.StudentCreateRq;
import ru.ersted.module_2vertx.dto.generated.StudentUpdateRq;
import ru.ersted.util.ParserUtil;

public class StudentHandler {

    private final StudentService studentService;

    private final EnrollmentService enrollmentService;

    public StudentHandler(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }


    public void create(RoutingContext context) {
        JsonObject json = context.body().asJsonObject();
        StudentCreateRq rq = json.mapTo(StudentCreateRq.class);

        studentService.create(rq)
                .onSuccess(student -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.CREATED.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(student).encode());
                });

    }

    public void getAll(RoutingContext context) {
        int limit = ParserUtil.parseIntOrDefault(context.request().getParam("limit"), 10);
        int offset = ParserUtil.parseIntOrDefault(context.request().getParam("offset"), 0);

        studentService.getAll(limit, offset)
                .onSuccess(students -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(students).encode());
                });

    }

    public void findById(RoutingContext context) {
        Long studentId = Long.parseLong(context.pathParam("id"));

        //todo добавить 404 Not Found
        studentService.findById(studentId)
                .onSuccess(student -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(student).encode());
                });

    }

    public void update(RoutingContext context) {
        JsonObject json = context.body().asJsonObject();
        StudentUpdateRq rq = json.mapTo(StudentUpdateRq.class);

        Long id = Long.parseLong(context.pathParam("id"));

        studentService.update(id, rq)
                .onSuccess(student -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(student).encode());
                });

    }

    public void delete(RoutingContext context) {
        Long id = Long.parseLong(context.pathParam("id"));

        studentService.delete(id)
                .onSuccess(nothing -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom("Student deleted successfully").encode());
                });

    }

    public void addCourse(RoutingContext context) {
        Long courseId = Long.parseLong(context.pathParam("courseId"));
        Long studentId = Long.parseLong(context.pathParam("studentId"));

        enrollmentService.enrollStudentToCourse(studentId, courseId)
                .onSuccess(student -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(student).encode());
                });

    }

    public void findCourses(RoutingContext context) {
        Long studentId = Long.parseLong(context.pathParam("studentId"));

        enrollmentService.findCoursesByStudentId(studentId)
                .onSuccess(student -> {
                    context.response()
                            .setStatusCode(HttpResponseStatus.OK.code())
                            .putHeader("content-type", "application/json")
                            .end(JsonObject.mapFrom(student).encode());
                });

    }

}
