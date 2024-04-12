package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;

import java.util.List;
import java.util.Set;

public interface StudentService {
    Student getStudent(Long id);
    List<Student> getStudents();
    Student saveStudent(Student student);
    void deleteStudent(Long id);
    Student updateStudent(Student student, Long id);
    Set<Course> getEnrolledCourses(Long id);
    List<Grade> getGradesByStudentId(Long studentId);
}
