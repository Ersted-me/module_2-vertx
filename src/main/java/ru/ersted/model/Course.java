package ru.ersted.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    private Long id;

    private String title;

    private Long teacherId;

    private Teacher teacher;

    private Set<Student> students = new HashSet<>();

}
