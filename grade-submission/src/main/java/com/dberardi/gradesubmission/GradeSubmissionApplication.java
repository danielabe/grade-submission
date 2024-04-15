package com.dberardi.gradesubmission;

import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.repository.CourseRepository;
import com.dberardi.gradesubmission.repository.StudentRepository;
import com.dberardi.gradesubmission.service.CourseServiceImpl;
import com.dberardi.gradesubmission.service.GradeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootApplication
public class GradeSubmissionApplication implements CommandLineRunner {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private CourseServiceImpl courseService;

	@Autowired
	private GradeServiceImpl gradeService;

	public static void main(String[] args) {
		SpringApplication.run(GradeSubmissionApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Student student1 = new Student(1L, "Harry Potter", LocalDate.of(1980,07,31), Collections.emptyList(), Collections.emptySet());
		Student student2 = new Student("Ron Weasley", LocalDate.of(1980,03,01));
		Student student3 = new Student("Neville Longbottom", LocalDate.of(1980,07,30));

		Student[] students = new Student[] {
				student1, student2, student3
		};

		for (Student student : students) {
			studentRepository.save(student);
		}

		Course course1 = new Course(1L, "Subject1", "CODE1","Description1", Collections.emptyList(), Collections.emptySet());
		Course course2 = new Course("Subject2", "CODE2","Description2");
		Course course3 = new Course("Subject3", "CODE3","Description3");

		Course[] courses = new Course[] {
				course1, course2, course3
		};

		for (Course course : courses) {
			courseRepository.save(course);
		}

		courseService.enrollStudentToCourse(1L, 1L);
		courseService.enrollStudentToCourse(2L, 2L);
		courseService.enrollStudentToCourse(3L, 2L);

		Grade grade1 = new Grade(1L, "A");
		gradeService.saveGrade(grade1, 1L, 1L);

		Grade grade2 = new Grade(2L, "B");
		gradeService.saveGrade(grade2, 2L, 2L);

	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
