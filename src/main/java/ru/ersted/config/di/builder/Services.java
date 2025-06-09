package ru.ersted.config.di.builder;

import ru.ersted.service.*;

record Services(
        EnrollmentService enrollment,
        CourseService course,
        StudentService student,
        TeacherService teacher,
        DepartmentService department,
        CourseEnrichmentService courseEnrichment,
        StudentEnrichmentService studentEnrichment,
        TeacherEnrichmentService teacherEnrichment) {
}
