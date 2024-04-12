package com.dberardi.gradesubmission.repository;

import com.dberardi.gradesubmission.model.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
}
