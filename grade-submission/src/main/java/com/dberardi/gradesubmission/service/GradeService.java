package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.model.Grade;

import java.util.List;

public interface GradeService {
    Grade getGrade(Long courseId, Long studentId);
    List<Grade> getGrades();
    Grade saveGrade(Grade grade, Long courseId, Long studentId);
    Grade updateGrade(Grade grade, Long courseId, Long studentId);
    void deleteGrade(Long courseId, Long studentId);
    List<Grade> getCourseGrades(Long courseId);
}
