package com.dberardi.gradesubmission.service;


import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.exception.StudentNotEnrolledException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.CourseRepository;
import com.dberardi.gradesubmission.repository.GradeRepository;
import com.dberardi.gradesubmission.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class GradeServiceImpl implements GradeService {

    GradeRepository gradeRepository;
    CourseRepository courseRepository;
    StudentRepository studentRepository;

    @Override
    public Grade getGrade(Long courseId, Long studentId) {
        Optional<Grade> gradeOptional = gradeRepository.findByCourseIdAndStudentId(courseId, studentId);
        if(gradeOptional.isPresent()) return gradeOptional.get();
        else throw new EntityNotFoundException(courseId, studentId, Grade.class);
    }

    @Override
    public List<Grade> getGrades() {
        return (List<Grade>) gradeRepository.findAll();
    }

    @Transactional
    @Override
    public Grade saveGrade(Grade grade, Long courseId, Long studentId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if(!courseOptional.isPresent()) throw new EntityNotFoundException(courseId, Course.class);

        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if(!studentOptional.isPresent()) throw new EntityNotFoundException(studentId, Student.class);

        System.out.println(studentOptional);
        System.out.println(courseOptional);
        if(studentOptional.get().getCourses().contains(courseOptional.get())) {
            grade.setCourse(courseOptional.get());
            grade.setStudent(studentOptional.get());
        } else throw new StudentNotEnrolledException(courseId, studentId);

        return gradeRepository.save(grade);
    }

    @Override
    public void deleteGrade(Long courseId, Long studentId) {
        getGrade(courseId, studentId);
        gradeRepository.deleteByCourseIdAndStudentId(courseId, studentId);
    }

    @Override
    public List<Grade> getCourseGrades(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if(courseOptional.isPresent()) return courseOptional.get().getGrades();
        else throw new EntityNotFoundException(courseId, Course.class);
    }
}
