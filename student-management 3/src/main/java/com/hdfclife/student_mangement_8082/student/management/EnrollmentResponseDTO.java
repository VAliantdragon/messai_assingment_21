package com.hdfclife.student_mangement_8082.student.management;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EnrollmentResponseDTO {
    private String studentName;
    private List<String> enrolledCourses;
}
