package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    StudentRepository studentRepository;

    @Override
    public Student getStudent(Long id) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        if(studentOptional.isPresent()) return studentOptional.get();
        else throw new EntityNotFoundException(id, Student.class);
    }

    @Override
    public List<Student> getStudents() {
        return (List<Student>) studentRepository.findAll();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Student newStudent, Long id) {
        Student student = getStudent(id);
        student.setName(newStudent.getName());
        student.setBirthDate(newStudent.getBirthDate());
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {
        getStudent(id);
        studentRepository.deleteById(id);
    }

    @Override
    public Set<Course> getEnrolledCourses(Long id) {
        return getStudent(id).getCourses();
    }

    @Override
    public List<Grade> getGradesByStudentId(Long studentId) {
        return getStudent(studentId).getGrades();
    }
}
