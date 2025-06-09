package ru.ersted.config.di.builder;

import ru.ersted.repository.*;

record Repositories(
        EnrollmentRepository enrollment,
        CourseRepository course,
        StudentRepository student,
        TeacherRepository teacher,
        DepartmentRepository department) {
}
