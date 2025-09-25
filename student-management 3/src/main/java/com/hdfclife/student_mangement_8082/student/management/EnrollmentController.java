package com.hdfclife.student_mangement_8082.student.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll/{studentId}/{courseId}")
    public ResponseEntity<?> enrollStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        return enrollmentService.enrollStudent(studentId, courseId);
    }

    @GetMapping("/enrollments/{studentId}")
    public ResponseEntity<?> getEnrollments(@PathVariable Long studentId) {
        return enrollmentService.getEnrollments(studentId);
    }
}
