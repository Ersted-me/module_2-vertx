package ru.ersted.config.di.builder;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import ru.ersted.config.database.DefaultDatabaseClientFactory;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.handler.CourseHandler;
import ru.ersted.handler.DepartmentHandler;
import ru.ersted.handler.StudentHandler;
import ru.ersted.handler.TeacherHandler;
import ru.ersted.repository.*;
import ru.ersted.service.*;

public final class AppComponentBuilder {

    public static ServiceContainer build(Vertx vertx, JsonObject cfg) {
        Pool db = buildDatabaseClient(vertx, cfg);

        Repositories repositories = buildRepositories(db);
        Services services = buildServices(repositories);
        Handlers handlers = buildHandlers(services);

        return new ServiceContainer.Builder()
                .databaseClient(db)
                .courseHandler(handlers.course())
                .studentHandler(handlers.student())
                .departmentHandler(handlers.department())
                .teacherHandler(handlers.teacher())
                .build();
    }


    private static Pool buildDatabaseClient(Vertx v, JsonObject cfg) {
        return DefaultDatabaseClientFactory.createClient(v, cfg);
    }


    private static Repositories buildRepositories(Pool db) {
        return new Repositories(
                new EnrollmentRepository(db),
                new CourseRepository(db),
                new StudentRepository(db),
                new TeacherRepository(db),
                new DepartmentRepository(db)
        );
    }


    private static Services buildServices(Repositories repositories) {

        CourseEnrichmentService courseEnrich = new CourseEnrichmentService();
        StudentEnrichmentService studentEnrich = new StudentEnrichmentService();
        TeacherEnrichmentService teacherEnrich = new TeacherEnrichmentService();

        EnrollmentService enrollment = new EnrollmentService(repositories.enrollment());
        CourseService course = new CourseService(repositories.course(), courseEnrich);
        StudentService student = new StudentService(repositories.student());
        TeacherService teacher = new TeacherService(repositories.teacher());
        DepartmentService department = new DepartmentService(repositories.department());


        courseEnrich.setEnrollmentService(enrollment);
        courseEnrich.setTeacherService(teacher);

        studentEnrich.setEnrollmentService(enrollment);
        studentEnrich.setCourseEnrichmentService(courseEnrich);

        enrollment.setStudentService(student);
        enrollment.setCourseEnrichmentService(courseEnrich);

        teacherEnrich.setCourseService(course);
        teacherEnrich.setDepartmentService(department);

        student.setStudentEnrichmentService(studentEnrich);
        teacher.setTeacherEnrichmentService(teacherEnrich);

        return new Services(enrollment, course, student, teacher, department,
                courseEnrich, studentEnrich, teacherEnrich);
    }


    private static Handlers buildHandlers(Services services) {
        CourseHandler courseHandler = new CourseHandler(services.course());
        StudentHandler studentHandler = new StudentHandler(services.student(), services.enrollment());
        DepartmentHandler departmentHandler = new DepartmentHandler(services.department());
        TeacherHandler teacherHandler = new TeacherHandler(services.teacher());
        teacherHandler.setCourseService(services.course());

        return new Handlers(courseHandler, studentHandler, departmentHandler, teacherHandler);
    }

}