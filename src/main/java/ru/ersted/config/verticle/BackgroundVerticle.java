package ru.ersted.config.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.config.di.Services;
import ru.ersted.config.server.ServerConfig;
import ru.ersted.module_2vertx.dto.generated.*;
import ru.ersted.service.*;
import ru.ersted.util.Paging;

import static ru.ersted.constant.EventBusAddress.*;
import static ru.ersted.util.EventBusConsumerUtil.register;

public class BackgroundVerticle extends AbstractVerticle {

    private final CourseService courseService;

    private final StudentService studentService;

    private final DepartmentService departmentService;

    private final TeacherService teacherService;

    private final EnrollmentService enrollmentService;

    private final ServerConfig serverConfig;


    public BackgroundVerticle(ServiceContainer container, ServerConfig serverConfig) {
        Services services = container.getServices();
        this.enrollmentService = services.enrollment();
        this.courseService = services.course();
        this.studentService = services.student();
        this.departmentService = services.department();
        this.teacherService = services.teacher();
        this.serverConfig = serverConfig;
    }


    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        // Course
        register(COURSE_GET_ALL, vertx, body -> {
            int limit = body.getInteger("limit", Paging.DEFAULT_LIMIT);
            int offset = body.getInteger("offset", Paging.DEFAULT_OFFSET);

            return courseService.getAll(limit, offset)
                    .map(list -> list.stream()
                            .map(JsonObject::mapFrom)
                            .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
        });

        register(COURSE_CREATE, vertx, body -> {
            CourseCreateRq courseRq = body.mapTo(CourseCreateRq.class);

            return courseService.create(courseRq)
                    .map(JsonObject::mapFrom);
        });


        // Department
        register(DEPARTMENT_CREATE, vertx, body -> {
            DepartmentCreateRq rq = body.mapTo(DepartmentCreateRq.class);

            return departmentService.create(rq)
                    .map(JsonObject::mapFrom);
        });

        register(DEPARTMENT_ASSIGNING_HEAD_OF_DEPARTMENT, vertx, body -> {
            Long departmentId = body.getLong("departmentId");
            Long teacherId = body.getLong("teacherId");

            return departmentService.assigningHeadOfDepartment(departmentId, teacherId)
                    .map(JsonObject::mapFrom);
        });


        // Teacher
        register(TEACHER_CREATE, vertx, body -> {
            TeacherCreateRq rq = body.mapTo(TeacherCreateRq.class);

            return teacherService.create(rq)
                    .map(JsonObject::mapFrom);
        });

        register(TEACHER_ASSIGNING_TEACHER_TO_COURSE, vertx, body -> {
            Long coursesId = body.getLong("coursesId");
            Long teacherId = body.getLong("teacherId");

            return courseService.assigningTeacher(coursesId, teacherId)
                    .map(JsonObject::mapFrom);
        });

        register(TEACHER_GET_ALL, vertx, body -> {
            int limit = body.getInteger("limit", Paging.DEFAULT_LIMIT);
            int offset = body.getInteger("offset", Paging.DEFAULT_OFFSET);

            return teacherService.getAll(limit, offset)
                    .map(list -> list.stream()
                            .map(JsonObject::mapFrom)
                            .collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
                    );
        });


        //Student
        register(STUDENT_CREATE, vertx, body -> {
            StudentCreateRq rq = body.mapTo(StudentCreateRq.class);

            return studentService.create(rq)
                    .map(JsonObject::mapFrom);
        });

        register(STUDENT_GET_ALL, vertx, body -> {
            int limit = body.getInteger("limit", Paging.DEFAULT_LIMIT);
            int offset = body.getInteger("offset", Paging.DEFAULT_OFFSET);

            return studentService.getAll(limit, offset)
                    .map(list -> list.stream()
                            .map(JsonObject::mapFrom)
                            .collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
                    );
        });

        register(STUDENT_FIND_BY_ID, vertx, body -> {
            Long id = body.getLong("id");

            return studentService.findById(id)
                    .map(JsonObject::mapFrom);
        });

        register(STUDENT_UPDATE, vertx, body -> {
            StudentUpdateRq rq = body.getJsonObject("body").mapTo(StudentUpdateRq.class);
            Long id = body.getLong("id");

            return studentService.update(id, rq)
                    .map(JsonObject::mapFrom);
        });

        register(STUDENT_DELETE, vertx, body -> {
            Long id = body.getLong("id");

            return studentService.delete(id)
                    .map(e -> "Student deleted successfully")
                    .map(JsonObject::mapFrom);
        });

        register(STUDENT_ADD_COURSE, vertx, body -> {
            Long courseId = body.getLong("courseId");
            Long studentId = body.getLong("studentId");

            return enrollmentService.enrollStudentToCourse(studentId, courseId)
                    .map(JsonObject::mapFrom);
        });


        register(STUDENT_FIND_COURSES, vertx, body -> {

            Long studentId = body.getLong("studentId");

            return enrollmentService.findCoursesByStudentId(studentId)
                    .map(JsonObject::mapFrom);
        });


        startPromise.complete();
    }

}
