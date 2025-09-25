package com.hdfclife.student_mangement_8082.student.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<?> enrollStudent(Long studentId, Long courseId) {
        // Validate student existence
        try {
            restTemplate.getForObject("http://localhost:8081/students/{id}", String.class, studentId);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.status(404).body(new com.example.enrollmentservice.dto.ErrorResponse("Student not found"));
        }

        // Validate course existence
        try {
            restTemplate.getForObject("http://localhost:8082/courses/{id}", String.class, courseId);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.status(404).body(new com.example.enrollmentservice.dto.ErrorResponse("Course not found"));
        }

        // Check if enrollment already exists
        if (enrollmentRepository.findByStudentId(studentId).stream()
                .anyMatch(e -> e.getCourseId().equals(courseId))) {
            return ResponseEntity.status(400).body(new com.example.enrollmentservice.dto.ErrorResponse("Student already enrolled in this course"));
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollmentRepository.save(enrollment);

        return ResponseEntity.status(201).build();
    }

    public ResponseEntity<?> getEnrollments(Long studentId) {
        // Validate student existence
        try {
            restTemplate.getForObject("http://localhost:8081/students/{id}", String.class, studentId);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.status(404).body(new com.example.enrollmentservice.dto.ErrorResponse("Student not found"));
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<String> enrolledCourses = enrollments.stream()
                .map(enrollment -> {
                    try {
                        ResponseEntity<com.example.enrollmentservice.model.Course> response =
                                restTemplate.getForEntity("http://localhost:8082/courses/{id}", com.example.enrollmentservice.model.Course.class, enrollment.getCourseId());
                        return response.getBody().getCourseName();
                    } catch (HttpClientErrorException.NotFound ex) {
                        return "Unknown Course";
                    }
                })
                .collect(Collectors.toList());

        // Get student name (optional, but good for a rich response)
        ResponseEntity<StudentDTO> studentResponse = restTemplate.getForEntity("http://localhost:8081/students/{id}", StudentDTO.class, studentId);
        String studentName = studentResponse.getBody().getName();

        EnrollmentResponseDTO responseDTO = new EnrollmentResponseDTO();
        responseDTO.setStudentName(studentName);
        responseDTO.setEnrolledCourses(enrolledCourses);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
