package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.EntityAlreadyExistsException;
import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.CourseRepository;
import com.dberardi.gradesubmission.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentServiceImpl studentService;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    public void getCourse() {
        Course course = new Course("Course1", "CODE1", "Description");
        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));

        Course result = courseService.getCourse(0L);

        assertEquals("Course1", result.getSubject());
    }

    @Test
    public void getCourseNotFound() {
        assertThrows(EntityNotFoundException.class, () -> courseService.getCourse(0L));
    }

    @Test
    public void getCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(
                new Course("Subject1", "CODE1", "Description1"),
                new Course("Subject2", "CODE2", "Description2"),
                new Course("Subject3", "CODE3", "Description3")
        ));

        List<Course> result = courseService.getCourses();

        assertEquals("Subject1", result.get(0).getSubject());
        assertEquals(3, result.size());
    }

    @Test
    public void saveCourse() {
        Course course = new Course("Subject1", "CODE1","Description1");
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseService.saveCourse(course);

        verify(courseRepository, times(1)).save(any(Course.class));

        assertEquals("Subject1", result.getSubject());
        assertEquals("CODE1", result.getCode());
        assertEquals("Description1", result.getDescription());
    }

    @Test
    public void saveCourseRepeatedCode() {
        Course course = new Course("Subject1", "CODE1","Description1");
        List<Course> courses = Arrays.asList(course);

        when(courseService.getCourses()).thenReturn(courses);

        assertThrows(EntityAlreadyExistsException.class, () -> courseService.saveCourse(course));
    }

    @Test
    public void updateCourse() {
        Course oldCourse = new Course("Subject1", "CODE1", "Description1");
        Course newCourse = new Course("Subject2", "CODE2", "Description2");

        when(courseRepository.findById(0L)).thenReturn(Optional.of(oldCourse));

        courseService.updateCourse(newCourse, 0L);

        verify(courseRepository, times(1)).save(any(Course.class));

        assertEquals("Subject2", oldCourse.getSubject());
        assertEquals("CODE2", oldCourse.getCode());
        assertEquals("Description2", oldCourse.getDescription());
    }

    @Test
    public void updateCourseRepeatedCode() {
        Course course1 = new Course(0L, "Subject1", "CODE1", "Description1", Collections.emptyList(), new HashSet<>());
        Course course2 = new Course(1L, "Subject2", "CODE2", "Description2", Collections.emptyList(), new HashSet<>());
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course1));
        when(courseService.getCourses()).thenReturn(courses);

        assertThrows(EntityAlreadyExistsException.class, () -> courseService.updateCourse(course2, 0L));
    }

    @Test
    public void deleteCourse() {
        Course course =  new Course("Subject1", "CODE1", "Description1");
        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));

        courseService.deleteCourse(0L);

        verify(courseRepository, atLeastOnce()).deleteById(0L);
    }

    @Test
    public void getEnrolledStudents() {
        Student student = new Student("Harry Potter", LocalDate.of(1980, 07, 31));
        Course course = new Course(0L, "Subject1", "CODE1", "Description1", Collections.emptyList(), new HashSet<>(Arrays.asList(student)));

        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));

        Set<Student> result = courseService.getEnrolledStudents(0L);

        assertEquals(1, result.size());
    }

    @Test
    public void enrollStudentToCourse() {
        Student student = new Student(0L, "Harry Potter", LocalDate.of(1980, 7, 31), Collections.emptyList(), Collections.emptySet());
        Course course = new Course(0L, "Subject1", "CODE1", "Description1", Collections.emptyList(), new HashSet<>());

        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));
        when(studentService.getStudent(any(Long.class))).thenReturn(student);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course savedCourse = invocation.getArgument(0);
            savedCourse.getStudents().add(student);
            return savedCourse;
        });

        Course result = courseService.enrollStudentToCourse(0L, 0L);

        assertTrue(result.getStudents().contains(student));
    }

    @Test
    public void enrollStudentToCourseAlreadyEnrolled() {
        Student student = new Student(0L, "Harry Potter", LocalDate.of(1980, 7, 31), Collections.emptyList(), Collections.emptySet());
        Course course = new Course(0L, "Subject1", "CODE1", "Description1", Collections.emptyList(), new HashSet<>());

        course.getStudents().add(student);

        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));
        when(studentService.getStudent(any(Long.class))).thenReturn(student);

        assertThrows(EntityAlreadyExistsException.class, () -> courseService.enrollStudentToCourse(0L, 0L));
    }

    @Test
    public void unenrollStudentFromCourse() {
        Student student = new Student(0L, "Hermione Granger", LocalDate.of(1979, 9, 19), Collections.emptyList(), Collections.emptySet());
        Course course = new Course(0L, "Subject2", "CODE2", "Description2", Collections.emptyList(), new HashSet<>());

        course.getStudents().add(student);

        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));
        when(studentService.getStudent(any(Long.class))).thenReturn(student);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course savedCourse = invocation.getArgument(0);
            savedCourse.getStudents().remove(student);
            return savedCourse;
        });

        Course result = courseService.unenrollStudentFromCourse(0L, 0L);

        assertFalse(result.getStudents().contains(student));
    }

}
