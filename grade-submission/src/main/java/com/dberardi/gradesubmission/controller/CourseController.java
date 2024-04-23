package com.dberardi.gradesubmission.controller;

import com.dberardi.gradesubmission.exception.ErrorResponse;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Tag(name = "Course Controller", description = "Create and retrieve courses")
@AllArgsConstructor
@RestController
@RequestMapping("/course")
public class CourseController {

    CourseService courseService;

    @Operation(summary = "Get course based on ID", description = "Returns a course based on an ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of course", content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Failed to convert type", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.getCourse(id), HttpStatus.OK);
    }

    @Operation(summary = "Retrieves courses", description = "Provides a list of all courses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of courses", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Course.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    })
    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Course>> getCourses() {
        return new ResponseEntity<>(courseService.getCourses(), HttpStatus.OK);
    }

    @Operation(summary = "Create course", description = "Creates a course from the provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful course creation", content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    })
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> saveCourse(@Valid @RequestBody Course course) {
        return new ResponseEntity<>(courseService.saveCourse(course),HttpStatus.CREATED);
    }

    @Operation(summary = "Update course", description = "Updates a course from the provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful course modification", content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> updateCourse(@Valid @RequestBody Course course, @PathVariable Long id) {
        return new ResponseEntity<>(courseService.updateCourse(course, id), HttpStatus.OK);
    }

    @Operation(summary = "Delete course", description = "Deletes a course based on an ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful course removal", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get enrolled students", description = "Provides a list of all students enrolled to a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of enrolled students", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Student.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value = "{id}/students", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Student>> getEnrolledStudents(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.getEnrolledStudents(id), HttpStatus.OK);
    }

    @Operation(summary = "Enroll a student", description = "Enrolls a student to a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful enrollment", content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(value = "{courseId}/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> enrollStudentToCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(courseService.enrollStudentToCourse(courseId, studentId), HttpStatus.OK);
    }

    @Operation(summary = "Unenroll a student", description = "Unenrolls a student from a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful unenrollment", content = @Content(schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course or student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(value = "{courseId}/student/{studentId}/unenroll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> unenrollStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(courseService.unenrollStudentFromCourse(courseId, studentId), HttpStatus.OK);
    }

}
