package ru.ersted.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import ru.ersted.module_2vertx.dto.generated.StudentCreateRq;
import ru.ersted.module_2vertx.dto.generated.StudentUpdateRq;
import ru.ersted.service.EnrollmentService;
import ru.ersted.service.StudentService;
import ru.ersted.util.ApiResponse;
import ru.ersted.util.ParserUtils;

@RequiredArgsConstructor
public class StudentHandler {

    private final StudentService studentService;

    private final EnrollmentService enrollmentService;


    public void create(RoutingContext context) {
        StudentCreateRq rq = context.body().asJsonObject().mapTo(StudentCreateRq.class);

        studentService.create(rq)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.CREATED, element))
                .onFailure(context::fail);

    }

    public void getAll(RoutingContext context) {
        int limit = ParserUtils.parseIntOrDefault(context.request().getParam("limit"), 10);
        int offset = ParserUtils.parseIntOrDefault(context.request().getParam("offset"), 0);

        studentService.getAll(limit, offset)
                .onSuccess(list -> ApiResponse.send(context, HttpResponseStatus.OK, list))
                .onFailure(context::fail);

    }

    public void findById(RoutingContext context) {
        Long studentId = Long.parseLong(context.pathParam("id"));

        studentService.findById(studentId)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.OK, element))
                .onFailure(context::fail);

    }

    public void update(RoutingContext context) {
        StudentUpdateRq rq = context.body().asJsonObject().mapTo(StudentUpdateRq.class);
        Long id = Long.parseLong(context.pathParam("id"));

        studentService.update(id, rq)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.OK, element))
                .onFailure(context::fail);

    }

    public void delete(RoutingContext context) {
        Long id = Long.parseLong(context.pathParam("id"));

        studentService.delete(id)
                .onSuccess(nothing -> ApiResponse.send(context, HttpResponseStatus.OK, "Student deleted successfully"))
                .onFailure(context::fail);

    }

    public void addCourse(RoutingContext context) {
        Long courseId = Long.parseLong(context.pathParam("courseId"));
        Long studentId = Long.parseLong(context.pathParam("studentId"));

        enrollmentService.enrollStudentToCourse(studentId, courseId)
                .onSuccess(element -> ApiResponse.send(context, HttpResponseStatus.OK, element))
                .onFailure(context::fail);

    }

    public void findCourses(RoutingContext context) {
        Long studentId = Long.parseLong(context.pathParam("studentId"));

        enrollmentService.findCoursesByStudentId(studentId)
                .onSuccess(list -> ApiResponse.send(context, HttpResponseStatus.OK, list))
                .onFailure(context::fail);
    }

}
