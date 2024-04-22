package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;
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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    public void getStudent() {
        when(studentRepository.findById(0L)).thenReturn(
                Optional.of(new Student("Harry Potter", LocalDate.of(1980,07,31)))
        );

        Student result = studentService.getStudent(0L);

        assertEquals("Harry Potter", result.getName());
        assertEquals(LocalDate.of(1980,07,31), result.getBirthDate());
    }

    @Test
    public void getStudentNotFound() {
        assertThrows(EntityNotFoundException.class, () -> studentService.getStudent(999999L));
    }

    @Test
    public void getStudents() {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(
                new Student("Harry Potter", LocalDate.of(1980,07,31)),
                new Student("Ron Weasley", LocalDate.of(1980,03,01)),
                new Student("Neville Longbottom", LocalDate.of(1980,07,30))
        ));

        List<Student> result = studentService.getStudents();

        assertEquals("Harry Potter", result.get(0).getName());
        assertEquals(LocalDate.of(1980,07,31), result.get(0).getBirthDate());
        assertEquals(3, result.size());
    }

    @Test
    public void saveStudent() {
        when(studentRepository.save(any(Student.class)))
                .thenReturn(new Student("Harry Potter", LocalDate.of(1980, 07, 31)));

        Student result = studentService.saveStudent(new Student("Harry Potter", LocalDate.of(1980, 07, 31)));

        verify(studentRepository, times(1)).save(any(Student.class));

        assertEquals("Harry Potter", result.getName());
    }

    @Test
    public void updateStudent() {
        Student student =  new Student("Harry Potter", LocalDate.of(1980, 07, 31));
        when(studentRepository.findById(0L)).thenReturn(Optional.of(student));

        student.setName("Harry James Potter");
        studentService.updateStudent(student, 0L);

        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void deleteStudent() {
        Student student =  new Student("Harry Potter", LocalDate.of(1980, 07, 31));
        when(studentRepository.findById(0L)).thenReturn(Optional.of(student));

        studentService.deleteStudent(0L);

        verify(studentRepository, atLeastOnce()).deleteById(0l);
    }

    @Test
    public void deleteStudentDoesNotExist() {
        when(studentRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> studentService.deleteStudent(0L));

        verify(studentRepository, never()).deleteById(0L);
    }

    @Test
    public void getEnrolledCourses() {
        Course course = new Course("Subject", "Code", "Description");
        Student student =  new Student(0L, "Harry Potter", LocalDate.of(1980, 07, 31), Collections.emptyList(), new HashSet<>(Arrays.asList(course)));

        when(studentRepository.findById(0L)).thenReturn(Optional.of(student));

        Set<Course> courses = studentService.getEnrolledCourses(0L);

        assertTrue(courses.contains(course));
    }

    @Test
    public void getGradesByStudentId() {
        Grade grade = new Grade();
        grade.setScore("A+");
        Student student =  new Student(0L, "Harry Potter", LocalDate.of(1980, 07, 31), Arrays.asList(grade), Collections.emptySet());

        when(studentRepository.findById(0L)).thenReturn(Optional.of(student));

        List<Grade> grades = studentService.getGradesByStudentId(0L);

        assertTrue(grades.contains(grade));
    }

}
