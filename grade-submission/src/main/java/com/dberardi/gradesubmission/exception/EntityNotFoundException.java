package com.dberardi.gradesubmission.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, Class<?> entity) {
        super("The " + entity.getSimpleName().toLowerCase() + " with id '" + id + "' does not exist in our records");
    }

    public EntityNotFoundException(Long courseId, Long studentId, Class<?> entity) {
        super("The " + entity.getSimpleName().toLowerCase() + " with courseId '" + courseId + "' and studentId '" + studentId + "' does not exist in our records");
    }

}
