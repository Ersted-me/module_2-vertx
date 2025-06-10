package ru.ersted.config.di.builder;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import ru.ersted.config.TransactionalOperator;
import ru.ersted.config.database.DefaultDatabaseClientFactory;
import ru.ersted.config.di.ServiceContainer;
import ru.ersted.handler.CourseHandler;
import ru.ersted.handler.DepartmentHandler;
import ru.ersted.handler.StudentHandler;
import ru.ersted.handler.TeacherHandler;
import ru.ersted.repository.*;
import ru.ersted.repository.mapper.CourseRowMapper;
import ru.ersted.repository.mapper.DepartmentRowMapper;
import ru.ersted.repository.mapper.StudentRowMapper;
import ru.ersted.repository.mapper.TeacherRowMapper;
import ru.ersted.service.*;

public final class AppComponentBuilder {

    public static ServiceContainer build(Vertx vertx, JsonObject cfg) {
        Pool db = buildDatabaseClient(vertx, cfg);
        TransactionalOperator transactionalOperator = new TransactionalOperator(db);

        RowMappers rowMappers = buildRowMappers();
        Repositories repositories = buildRepositories(db, rowMappers);
        Services services = buildServices(repositories, transactionalOperator);
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

    private static RowMappers buildRowMappers() {
        return new RowMappers(
                new CourseRowMapper(),
                new StudentRowMapper(),
                new DepartmentRowMapper(),
                new TeacherRowMapper());
    }

    private static Repositories buildRepositories(Pool db, RowMappers mappers) {
        return new Repositories(
                new EnrollmentRepository(db, mappers.courseRowMapper(), mappers.studentRowMapper()),
                new CourseRepository(db, mappers.courseRowMapper()),
                new StudentRepository(db, mappers.studentRowMapper()),
                new TeacherRepository(db, mappers.teacherRowMapper()),
                new DepartmentRepository(db, mappers.departmentRowMapper())
        );
    }


    private static Services buildServices(Repositories repositories, TransactionalOperator transactionalOperator) {

        CourseEnrichmentService courseEnrich = new CourseEnrichmentService();
        StudentEnrichmentService studentEnrich = new StudentEnrichmentService();
        TeacherEnrichmentService teacherEnrich = new TeacherEnrichmentService();

        EnrollmentService enrollment = new EnrollmentService(repositories.enrollment(), transactionalOperator);
        CourseService course = new CourseService(repositories.course(), repositories.teacher(), courseEnrich, transactionalOperator);
        StudentService student = new StudentService(repositories.student());
        TeacherService teacher = new TeacherService(repositories.teacher());
        DepartmentService department = new DepartmentService(repositories.department(), repositories.teacher(), transactionalOperator);


        courseEnrich.setEnrollmentService(enrollment);
        courseEnrich.setTeacherService(teacher);

        studentEnrich.setEnrollmentService(enrollment);
        studentEnrich.setCourseEnrichmentService(courseEnrich);

        enrollment.setStudentService(student);
        enrollment.setCourseEnrichmentService(courseEnrich);
        enrollment.setCourseRepository(repositories.course());
        enrollment.setStudentRepository(repositories.student());

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
        TeacherHandler teacherHandler = new TeacherHandler(services.teacher(), services.course());

        return new Handlers(courseHandler, studentHandler, departmentHandler, teacherHandler);
    }

}