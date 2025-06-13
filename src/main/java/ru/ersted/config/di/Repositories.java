package ru.ersted.config.di;

import ru.ersted.repository.*;

public record Repositories(
        EnrollmentRepository enrollment,
        CourseRepository course,
        StudentRepository student,
        TeacherRepository teacher,
        DepartmentRepository department) {
}
