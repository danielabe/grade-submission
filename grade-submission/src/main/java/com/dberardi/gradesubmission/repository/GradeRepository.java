package com.dberardi.gradesubmission.repository;

import com.dberardi.gradesubmission.model.Grade;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GradeRepository extends CrudRepository<Grade, Long> {
    Optional<Grade> findByCourseIdAndStudentId(Long courseId, Long studentId);
    @Transactional
    void deleteByCourseIdAndStudentId(Long courseId, Long studentId);
}
