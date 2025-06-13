package ru.ersted.config.di;

import ru.ersted.service.*;

public record Services(
        EnrollmentService enrollment,
        CourseService course,
        StudentService student,
        TeacherService teacher,
        DepartmentService department,
        CourseEnrichmentService courseEnrichment,
        StudentEnrichmentService studentEnrichment,
        TeacherEnrichmentService teacherEnrichment) {
}
