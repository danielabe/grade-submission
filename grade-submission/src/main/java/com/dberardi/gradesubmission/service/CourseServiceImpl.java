package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.CourseNotFoundException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.CourseRepository;
import com.dberardi.gradesubmission.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    CourseRepository courseRepository;
    StudentRepository studentRepository;

    @Override
    public Course getCourse(Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isPresent()) return courseOptional.get();
        else throw new CourseNotFoundException(id);
    }

    @Override
    public List<Course> getCourses() {
        return (List<Course>) courseRepository.findAll();
    }

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Course newCourse, Long id) {
        Course course = getCourse(id);
        course.setCode(newCourse.getCode());
        course.setSubject(newCourse.getSubject());
        course.setDescription(newCourse.getDescription());
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        getCourse(id);
        courseRepository.deleteById(id);
    }

    @Override
    public Set<Student> getEnrolledStudents(Long id) {
        return getCourse(id).getStudents();
    }

    @Override
    public Course enrollStudentToCourse(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if(studentOptional.isPresent()) course.getStudents().add(studentOptional.get()); //falta else con exception, AGREGAR el curso al estudiante?
        return courseRepository.save(course);
    }
}
