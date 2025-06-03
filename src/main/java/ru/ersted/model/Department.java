package ru.ersted.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    private Long id;

    private String name;

    private Long headOfDepartmentId;

    private Teacher headOfDepartment;

}
