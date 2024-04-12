package com.dberardi.gradesubmission.controller;

import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.service.GradeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/grade")
public class GradeController {
    
    GradeService gradeService;

    @GetMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Grade> getGrade(@PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(gradeService.getGrade(courseId, studentId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Grade>> getGrades() {
        return new ResponseEntity<>(gradeService.getGrades(), HttpStatus.OK);
    }

    @PostMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Grade> saveGrade(@Valid @RequestBody Grade Grade, @PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(gradeService.saveGrade(Grade, courseId, studentId), HttpStatus.CREATED);
    }

    @PutMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Grade> updateGrade(@Valid @RequestBody Grade Grade, @PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(gradeService.updateGrade(Grade, courseId, studentId), HttpStatus.CREATED);
    }

    @DeleteMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<HttpStatus> deleteGrade(@PathVariable Long courseId, @PathVariable Long studentId) {
        gradeService.deleteGrade(courseId, studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("student/{studentId}") //repetido
    public ResponseEntity<List<Grade>> getStudentGrades(@PathVariable Long studentId) {
        return new ResponseEntity<>(gradeService.getStudentGrades(studentId), HttpStatus.OK);
    }

    @GetMapping("course/{courseId}")
    public ResponseEntity<List<Grade>> getCourseGrades(@PathVariable Long courseId) {
        return new ResponseEntity<>(gradeService.getCourseGrades(courseId), HttpStatus.OK);
    }
}
