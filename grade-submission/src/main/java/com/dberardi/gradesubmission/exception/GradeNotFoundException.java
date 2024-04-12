package com.dberardi.gradesubmission.exception;

public class GradeNotFoundException extends RuntimeException {

    public GradeNotFoundException(Long courseId, Long studentId) {
        super("The grade with courseId '" + courseId + "' and studentId '" + studentId + "' does not exist in our records");
    }

}
