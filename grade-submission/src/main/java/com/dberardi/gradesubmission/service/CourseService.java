package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Student;

import java.util.List;
import java.util.Set;

public interface CourseService {
    Course getCourse(Long id);
    List<Course> getCourses();
    Course saveCourse(Course course);
    Course updateCourse(Course course, Long id);
    void deleteCourse(Long id);
    Set<Student> getEnrolledStudents(Long id);
    Course enrollStudentToCourse(Long courseId, Long studentId);
}
