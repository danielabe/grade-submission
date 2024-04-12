package com.dberardi.gradesubmission.exception;

public class StudentNotEnrolledException extends RuntimeException {

    public StudentNotEnrolledException(Long courseId, Long studentId) {
        super("The student with id: '" + studentId + "' is not enrolled in the course with id: '" + courseId);
    }

}
