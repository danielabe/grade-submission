package com.dberardi.gradesubmission.controller;

import com.dberardi.gradesubmission.exception.ErrorResponse;
import com.dberardi.gradesubmission.model.Course;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.model.Student;
import com.dberardi.gradesubmission.service.StudentService;
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

@Tag(name = "Student Controller", description = "Create and retrieve students")
@AllArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {

    StudentService studentService;

    @Operation(summary = "Get student based on ID", description = "Returns a student based on an ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of student", content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Failed to convert type", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getStudent(id), HttpStatus.OK);
    }

    @Operation(summary = "Retrieves students", description = "Provides a list of all students")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of students", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Student.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    })
    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Student>> getStudents() {
        return new ResponseEntity<>(studentService.getStudents(), HttpStatus.OK);
    }

    @Operation(summary = "Create student", description = "Creates a student from the provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful student creation", content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    })
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Student> saveStudent(@Valid @RequestBody Student student) {
        return new ResponseEntity<>(studentService.saveStudent(student), HttpStatus.CREATED);
    }

    @Operation(summary = "Update student", description = "Updates a student from the provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful student modification", content = @Content(schema = @Schema(implementation = Student.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Student> updateStudent(@Valid @RequestBody Student student, @PathVariable Long id) {
        return new ResponseEntity<>(studentService.updateStudent(student, id), HttpStatus.OK);
    }

    @Operation(summary = "Delete student", description = "Deletes a student based on an ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful student removal", content = @Content),
            @ApiResponse(responseCode = "400", description = "Failed to convert type", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get courses from a student by ID", description = "Provides a list of all courses in which the student is enrolled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful recovery of courses in which the student is enrolled", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Course.class)))),
            @ApiResponse(responseCode = "400", description = "Failed to convert", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value =  "{id}/courses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Course>> getEnrolledCourses(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getEnrolledCourses(id), HttpStatus.OK);
    }

    @Operation(summary = "Get grades by student", description = "Provides a list of all grades of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful recovery of grades by student", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grade.class)))),
            @ApiResponse(responseCode = "400", description = "Failed to convert type", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Student doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value =  "{id}/grades", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getGradesByStudentId(id), HttpStatus.OK);
    }

}
