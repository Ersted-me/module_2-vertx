package ru.ersted;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import ru.ersted.config.DefaultDatabaseClientFactory;
import ru.ersted.handler.CourseHandler;
import ru.ersted.handler.DepartmentHandler;
import ru.ersted.handler.StudentHandler;
import ru.ersted.handler.TeacherHandler;
import ru.ersted.repository.*;
import ru.ersted.service.*;

public class DefaultVerticle extends AbstractVerticle {

    private static final Integer DEFAULT_APPLICATION_PORT = 8080;
    private final JsonObject config;

    public DefaultVerticle(JsonObject config) {
        super();
        this.config = config;
    }

    @Override
    public void start() throws Exception {

        Router mainRouter = Router.router(vertx);

        Pool databaseClient = DefaultDatabaseClientFactory.createClient(vertx, config);

        // Enrichment components
        CourseEnrichmentService courseEnrichmentService = new CourseEnrichmentService();
        StudentEnrichmentService studentEnrichmentService = new StudentEnrichmentService();
        TeacherEnrichmentService teacherEnrichmentService = new TeacherEnrichmentService();

        // Enrolment components
        EnrollmentRepository enrollmentRepository = new EnrollmentRepository(databaseClient);
        EnrollmentService enrollmentService = new EnrollmentService(enrollmentRepository);


        // courses API
        CourseRepository courseRepository = new CourseRepository(databaseClient);
        CourseService courseService = new CourseService(courseRepository, courseEnrichmentService);
        CourseHandler courseHandler = new CourseHandler(courseService);

        Router courseRouter = Router.router(vertx);
        courseRouter.post().handler(courseHandler::create);
        courseRouter.get().handler(courseHandler::getAll);

        mainRouter.route("/api/v1/courses/*").subRouter(courseRouter);


        // student API
        StudentRepository studentRepository = new StudentRepository(databaseClient);
        StudentService studentService = new StudentService(studentRepository);
        StudentHandler studentHandler = new StudentHandler(studentService, enrollmentService);

        Router studentRouter = Router.router(vertx);

        studentRouter.post().handler(studentHandler::create);
        studentRouter.get().handler(studentHandler::getAll);
        studentRouter.get("/:id").handler(studentHandler::findById);
        studentRouter.put("/:id").handler(studentHandler::update);
        studentRouter.delete("/:id").handler(studentHandler::delete);

        studentRouter.post("/:studentId/courses/:courseId").handler(studentHandler::addCourse);
        studentRouter.get("/:studentId/courses").handler(studentHandler::findCourses);

        mainRouter.route("/api/v1/students/*").subRouter(studentRouter);


        // department API
        DepartmentRepository departmentRepository = new DepartmentRepository(databaseClient);
        DepartmentService departmentService = new DepartmentService(departmentRepository);
        DepartmentHandler departmentHandler = new DepartmentHandler(departmentService);

        Router departmentRouter = Router.router(vertx);
        departmentRouter.post().handler(departmentHandler::create);
        departmentRouter.post("/:departmentId/teacher/:teacherId").handler(departmentHandler::assigningHeadOfDepartment);

        mainRouter.route("/api/v1/departments/*").subRouter(departmentRouter);


        // teacher API
        TeacherRepository teacherRepository = new TeacherRepository(databaseClient);
        TeacherService teacherService = new TeacherService(teacherRepository);
        TeacherHandler teacherHandler = new TeacherHandler(teacherService);

        Router teacherRouter = Router.router(vertx);

        teacherRouter.post().handler(teacherHandler::create);
        teacherRouter.post("/:teacherId/courses/:coursesId").handler(teacherHandler::assigningTeacherToCourse);
        teacherRouter.get().handler(teacherHandler::findAll);

        mainRouter.route("/api/v1/teachers/*").subRouter(teacherRouter);


        // Dependencies
        courseEnrichmentService.setEnrollmentService(enrollmentService);
        courseEnrichmentService.setTeacherService(teacherService);

        studentEnrichmentService.setEnrollmentService(enrollmentService);
        studentEnrichmentService.setCourseEnrichmentService(courseEnrichmentService);

        enrollmentService.setStudentService(studentService);
        enrollmentService.setCourseEnrichmentService(courseEnrichmentService);

        teacherEnrichmentService.setCourseService(courseService);
        teacherEnrichmentService.setDepartmentService(departmentService);

        studentService.setStudentEnrichmentService(studentEnrichmentService);

        teacherService.setTeacherEnrichmentService(teacherEnrichmentService);
        teacherHandler.setCourseService(courseService);

        mainRouter.get("/hello").handler(ctx -> {
            ctx.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello, World!");
        });

        vertx.createHttpServer()
                .requestHandler(mainRouter)
                .listen(getHttpPort(), http -> {
                    if (http.succeeded()) {
                        System.out.println("HTTP server started on http://localhost:" + http.result().actualPort());
                    } else {
                        System.out.println("HTTP server failed to start: " + http.cause());
                    }
                });
    }

    private Integer getHttpPort() {
        return config.getJsonObject("server").getJsonObject("http").getInteger("port", DEFAULT_APPLICATION_PORT);
    }

}
