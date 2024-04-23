package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    CourseRepository courseRepository;
    StudentServiceImpl studentService;

    @Override
    public Course getCourse(Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if(courseOptional.isPresent()) return courseOptional.get();
        else throw new EntityNotFoundException(id, Course.class);
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
        Student student = studentService.getStudent(studentId);
        course.getStudents().add(student);
        return courseRepository.save(course);
    }

    @Override
    public Course unenrollStudentFromCourse(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        Student student = studentService.getStudent(studentId);
        course.getStudents().remove(student);
        return courseRepository.save(course);
    }
}
