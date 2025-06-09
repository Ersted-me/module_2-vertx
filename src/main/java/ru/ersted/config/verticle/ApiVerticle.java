package ru.ersted.config.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.config.server.ServerConfig;
import ru.ersted.exception.handler.GlobalErrorHandler;
import ru.ersted.handler.CourseHandler;
import ru.ersted.handler.DepartmentHandler;
import ru.ersted.handler.StudentHandler;
import ru.ersted.handler.TeacherHandler;

import java.util.function.Consumer;

@Slf4j
public final class ApiVerticle extends AbstractVerticle {

    private static final String API_V1_PATTERN = "/api/v1/%s/*";

    private final CourseHandler courseHandler;
    private final StudentHandler studentHandler;
    private final DepartmentHandler departmentHandler;
    private final TeacherHandler teacherHandler;

    private final ServerConfig serverConfig;

    public ApiVerticle(ServiceContainer container, ServerConfig serverConfig) {
        this.courseHandler = container.getCourseHandler();
        this.studentHandler = container.getStudentHandler();
        this.departmentHandler = container.getDepartmentHandler();
        this.teacherHandler = container.getTeacherHandler();
        this.serverConfig = serverConfig;
    }

    @Override
    public void start() {
        Router main = Router.router(vertx);

        registerRoutes(main);

        vertx.createHttpServer()
                .requestHandler(main)
                .listen(serverConfig.getHttpPort(), ar -> {
                    if (ar.succeeded()) {
                        log.info("API server started on port {}", ar.result().actualPort());
                    } else {
                        log.error("Failed to start API server", ar.cause());
                    }
                });
    }


    private void registerRoutes(Router main) {
        main.route().failureHandler(new GlobalErrorHandler());

        mount(main, API_V1_PATTERN.formatted("courses"), r -> {
            r.post().handler(courseHandler::create);
            r.get().handler(courseHandler::getAll);
        });

        mount(main, API_V1_PATTERN.formatted("students"), r -> {
            r.post().handler(studentHandler::create);
            r.get().handler(studentHandler::getAll);
            r.get("/:id").handler(studentHandler::findById);
            r.put("/:id").handler(studentHandler::update);
            r.delete("/:id").handler(studentHandler::delete);
            r.post("/:studentId/courses/:courseId").handler(studentHandler::addCourse);
            r.get("/:studentId/courses").handler(studentHandler::findCourses);
        });

        mount(main, API_V1_PATTERN.formatted("departments"), r -> {
            r.post().handler(departmentHandler::create);
            r.post("/:departmentId/teacher/:teacherId")
                    .handler(departmentHandler::assigningHeadOfDepartment);
        });

        mount(main, API_V1_PATTERN.formatted("teachers"), r -> {
            r.post().handler(teacherHandler::create);
            r.post("/:teacherId/courses/:coursesId").handler(teacherHandler::assigningTeacherToCourse);
            r.get().handler(teacherHandler::findAll);
        });
    }

    private void mount(Router root, String path, Consumer<Router> configurer) {
        Router sub = Router.router(vertx);
        configurer.accept(sub);
        root.route(path).subRouter(sub);
    }

}
