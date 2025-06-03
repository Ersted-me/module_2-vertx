package ru.ersted.service;

import io.vertx.core.Future;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ersted.model.Teacher;

import java.util.HashSet;

@Setter
@NoArgsConstructor
public class TeacherEnrichmentService {

    private DepartmentService departmentService;

    private CourseService courseService;


    public Future<Teacher> enrich(Teacher teacher) {
        return enrichWithCourses(teacher)
                .compose(this::enrichWithDepartment);
    }

    public Future<Teacher> enrichWithDepartment(Teacher teacher) {
        return departmentService.findById(teacher.getDepartmentId())
                .map(department -> {
                    teacher.setDepartment(department);
                    return teacher;
                });
    }

    public Future<Teacher> enrichWithCourses(Teacher teacher) {
        return courseService.findByTeacherId(teacher.getId())
                .map(courses -> {
                    teacher.setCourses(new HashSet<>(courses));
                    return teacher;
                });
    }

}
