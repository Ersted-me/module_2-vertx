package ru.ersted.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private Long id;

    private String name;

    private String email;

    private Set<Course> courses = new HashSet<>();


}
