package ru.ersted.config.di;

import io.vertx.sqlclient.Pool;
import lombok.Getter;
import ru.ersted.handler.CourseHandler;
import ru.ersted.handler.DepartmentHandler;
import ru.ersted.handler.StudentHandler;
import ru.ersted.handler.TeacherHandler;

@Getter
public final class ServiceContainer {

    private final Pool databaseClient;

    private final CourseHandler courseHandler;
    private final StudentHandler studentHandler;
    private final DepartmentHandler departmentHandler;
    private final TeacherHandler teacherHandler;

    private ServiceContainer(Builder builder) {
        this.databaseClient     = builder.databaseClient;
        this.courseHandler      = builder.courseHandler;
        this.studentHandler     = builder.studentHandler;
        this.departmentHandler  = builder.departmentHandler;
        this.teacherHandler     = builder.teacherHandler;
    }


    public static final class Builder {
        private Pool databaseClient;
        private CourseHandler courseHandler;
        private StudentHandler studentHandler;
        private DepartmentHandler departmentHandler;
        private TeacherHandler teacherHandler;

        public Builder databaseClient(Pool client)                     { this.databaseClient = client; return this; }
        public Builder courseHandler(CourseHandler handler)              { this.courseHandler = handler; return this; }
        public Builder studentHandler(StudentHandler handler)            { this.studentHandler = handler; return this; }
        public Builder departmentHandler(DepartmentHandler handler)      { this.departmentHandler = handler; return this; }
        public Builder teacherHandler(TeacherHandler handler)            { this.teacherHandler = handler; return this; }

        public ServiceContainer build() {

            if (databaseClient == null || courseHandler == null ||
                    studentHandler == null || departmentHandler == null || teacherHandler == null) {
                throw new IllegalStateException("All dependencies must be provided");
            }
            return new ServiceContainer(this);
        }

    }

}
