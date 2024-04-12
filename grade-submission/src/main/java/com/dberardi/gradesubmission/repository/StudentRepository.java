package com.dberardi.gradesubmission.repository;

import com.dberardi.gradesubmission.model.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
}
