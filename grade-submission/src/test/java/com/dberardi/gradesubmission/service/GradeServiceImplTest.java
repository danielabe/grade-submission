package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.exception.EntityNotFoundException;
import com.dberardi.gradesubmission.exception.StudentNotEnrolledException;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.CourseRepository;
import com.dberardi.gradesubmission.repository.GradeRepository;
import com.dberardi.gradesubmission.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Test
    public void getGrade() {
        Grade grade = new Grade();
        grade.setScore("A");

        when(gradeRepository.findByCourseIdAndStudentId(any(Long.class), any(Long.class))).thenReturn(Optional.of(grade));

        Grade result = gradeService.getGrade(0L, 0L);

        assertEquals("A", result.getScore());
    }

    @Test
    public void getGradeNotFound() {
        assertThrows(EntityNotFoundException.class, () -> gradeService.getGrade(0L,0L));
    }

    @Test
    public void getGrades() {
        when(gradeRepository.findAll()).thenReturn(Arrays.asList(
                new Grade(0L,"A",null,null),
                new Grade(1L,"B",null,null),
                new Grade(2L,"C",null,null)
        ));

        List<Grade> result = gradeService.getGrades();

        assertEquals("B", result.get(1).getScore());
        assertEquals(3, result.size());
    }

    @Test
    public void saveGrade() {
        Course course = new Course("Course1", "CODE1", "Description");
        Student student = new Student(0L, "Harry Potter", LocalDate.of(1980, 07, 31), Collections.emptyList(), new HashSet<>(Arrays.asList(course)));
        student.getCourses().add(course);

        when(gradeRepository.save(any(Grade.class)))
                .thenReturn(new Grade(0L,"A", student, course));
        when(studentRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(student));
        when(courseRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(course));

        Grade result = gradeService.saveGrade(new Grade(0L,"A", student, course), 0L, 0L);

        verify(gradeRepository, times(1)).save(any(Grade.class));

        assertEquals("A", result.getScore());
    }

    @Test
    public void saveGradeStudentNotEnrolled() {
        Course course = new Course("Course1", "CODE1", "Description");
        Student student = new Student(0L, "Harry Potter", LocalDate.of(1980, 07, 31), Collections.EMPTY_LIST, Collections.EMPTY_SET);

        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));
        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));

        assertThrows(StudentNotEnrolledException.class, () -> gradeService.saveGrade(new Grade(), 0L, 0L));
    }

    @Test
    public void updateGrade() {
        Grade grade =  new Grade(0L,"A",null,null);
        when(gradeRepository.findByCourseIdAndStudentId(0L, 0L)).thenReturn(Optional.of(grade));

        grade.setScore("F");
        gradeService.updateGrade(grade, 0L, 0l);

        verify(gradeRepository, times(1)).save(grade);
    }

    @Test
    public void deleteGrade() {
        Grade grade =  new Grade(0L,"A",null,null);
        when(gradeRepository.findByCourseIdAndStudentId(any(Long.class), any(Long.class))).thenReturn(Optional.of(grade));

        gradeService.deleteGrade(0L, 0L);

        verify(gradeRepository, atLeastOnce()).deleteByCourseIdAndStudentId(0L, 0L);
    }


    @Test
    public void getCourseGrades() {
        Course course = new Course("Course1", "CODE1", "Description");
        Grade grade =  new Grade(0L,"A",null,null);
        course.setGrades(new ArrayList<>(Arrays.asList(grade)));

        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(course));

        List<Grade> result = gradeService.getCourseGrades(0L);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getScore());
    }

    @Test
    public void getCourseGradesCourseNotFound() {
        assertThrows(EntityNotFoundException.class, () -> gradeService.getCourseGrades(0L));
    }
}