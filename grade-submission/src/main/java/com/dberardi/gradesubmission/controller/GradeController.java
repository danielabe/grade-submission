package com.dberardi.gradesubmission.controller;

import com.dberardi.gradesubmission.exception.ErrorResponse;
import com.dberardi.gradesubmission.model.Grade;
import com.dberardi.gradesubmission.service.GradeService;
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

@Tag(name = "Grade Controller", description = "Create and retrieve grades")
@AllArgsConstructor
@RestController
@RequestMapping("/grade")
public class GradeController {
    
    GradeService gradeService;

    @Operation(summary = "Get grade based on course and student ID", description = "Returns a grade based on course and student ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of grade", content = @Content(schema = @Schema(implementation = Grade.class))),
            @ApiResponse(responseCode = "400", description = "Failed to convert type", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "There is no grade for that course and student", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value = "course/{courseId}/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Grade> getGrade(@PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(gradeService.getGrade(courseId, studentId), HttpStatus.OK);
    }

    @Operation(summary = "Retrieves grades", description = "Provides a list of all grades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of grades", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grade.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    })
    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Grade>> getGrades() {
        return new ResponseEntity<>(gradeService.getGrades(), HttpStatus.OK);
    }

    @Operation(summary = "Create grade", description = "Creates a grade from the provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful grade creation", content = @Content(schema = @Schema(implementation = Grade.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "There is no grade for that course and student", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping(value = "course/{courseId}/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Grade> saveGrade(@Valid @RequestBody Grade Grade, @PathVariable Long courseId, @PathVariable Long studentId) {
        return new ResponseEntity<>(gradeService.saveGrade(Grade, courseId, studentId), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete grade", description = "Deletes a grade based on course and student ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful grade removal", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "There is no grade for that course and student", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping(value = "course/{courseId}/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> deleteGrade(@PathVariable Long courseId, @PathVariable Long studentId) {
        gradeService.deleteGrade(courseId, studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get course grades", description = "Provides a list of all grades of a course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of grades of a course", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grade.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Course doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(value = "course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Grade>> getCourseGrades(@PathVariable Long courseId) {
        return new ResponseEntity<>(gradeService.getCourseGrades(courseId), HttpStatus.OK);
    }
}
