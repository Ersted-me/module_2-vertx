package ru.ersted.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {

    private Long id;

    private String name;

    private Long departmentId;

    private Department department;

    private Set<Course> courses = new HashSet<>();

}
