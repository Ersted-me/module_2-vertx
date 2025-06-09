package ru.ersted.config.di.builder;

import ru.ersted.handler.CourseHandler;
import ru.ersted.handler.DepartmentHandler;
import ru.ersted.handler.StudentHandler;
import ru.ersted.handler.TeacherHandler;

record Handlers(
        CourseHandler course,
        StudentHandler student,
        DepartmentHandler department,
        TeacherHandler teacher) {
}
