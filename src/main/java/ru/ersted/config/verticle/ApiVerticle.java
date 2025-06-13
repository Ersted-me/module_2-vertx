package ru.ersted.config.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import ru.ersted.config.di.Handlers;
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
        Handlers handlers = container.getHandlers();

        this.courseHandler = handlers.course();
        this.studentHandler = handlers.student();
        this.departmentHandler = handlers.department();
        this.teacherHandler = handlers.teacher();
        this.serverConfig = serverConfig;
    }


    @Override
    public void start() {
        Router main = Router.router(vertx);
        main.route().handler(BodyHandler.create());

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

        mount(main, API_V1_PATTERN.formatted("courses"), router -> {
            router.post().handler(ctx -> courseHandler.create(ctx, vertx));
            router.get().handler(ctx -> courseHandler.getAll(ctx, vertx));
        });

        mount(main, API_V1_PATTERN.formatted("students"), router -> {
            router.post().handler(ctx -> studentHandler.create(ctx, vertx));
            router.get().handler(ctx -> studentHandler.getAll(ctx, vertx));
            router.get("/:id").handler(ctx -> studentHandler.findById(ctx, vertx));
            router.put("/:id").handler(ctx -> studentHandler.update(ctx, vertx));
            router.delete("/:id").handler(ctx -> studentHandler.delete(ctx, vertx));
            router.post("/:studentId/courses/:courseId").handler(ctx -> studentHandler.addCourse(ctx, vertx));
            router.get("/:studentId/courses").handler(ctx -> studentHandler.findCourses(ctx, vertx));
        });

        mount(main, API_V1_PATTERN.formatted("departments"), router -> {
            router.post().handler(ctx -> departmentHandler.create(ctx, vertx));
            router.post("/:departmentId/teacher/:teacherId")
                    .handler(ctx -> departmentHandler.assigningHeadOfDepartment(ctx, vertx));
        });

        mount(main, API_V1_PATTERN.formatted("teachers"), router -> {
            router.post().handler(ctx -> teacherHandler.create(ctx, vertx));
            router.post("/:teacherId/courses/:coursesId")
                    .handler(ctx -> teacherHandler.assigningTeacherToCourse(ctx, vertx));
            router.get().handler(ctx -> teacherHandler.findAll(ctx, vertx));
        });

    }

    private void mount(Router root, String path, Consumer<Router> configurer) {
        Router sub = Router.router(vertx);
        configurer.accept(sub);
        root.route(path).subRouter(sub);
    }

}
