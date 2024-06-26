package com.dberardi.gradesubmission;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dberardi.gradesubmission.model.User;
import com.dberardi.gradesubmission.security.SecurityConstants;
import jakarta.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@Sql(scripts = {"/db/init.sql"})
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
class GradeSubmissionApplicationTests {

	@Container
	MySQLContainer mySQLContainer = new MySQLContainer("mysql:5.7.24");

	@Autowired
	private MockMvc mockMvc;

	private String token;
	private String invalidToken;
	private User user;

	@BeforeEach
	public void setUp() {
		user = new User("User", "password123");
		token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
				.sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

		invalidToken = token + "e";
	}

	@Test
	void contextLoads() {
		assertNotNull(mockMvc);
	}

	@Test
	public void testGetStudent_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Harry Potter"))
				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
	}

	@Test
	public void testGetStudentNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetStudentWithNotValidType_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/a")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is4xxClientError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.message[0]").value(containsString("Failed to convert")));
	}

	@Test
	public void testGetStudents_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/all")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("Harry Potter"))
				.andExpect(jsonPath("$[0].birthDate").value("1980-07-31"));
	}

	@Test
	public void testSaveStudent_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Luna Lovegood");
		requestJson.put("birthDate", LocalDate.of(1981, 2, 13).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Luna Lovegood"))
				.andExpect(jsonPath("$.birthDate").value("1981-02-13"));
	}

	@Test
	public void testSaveStudentBadRequest_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", null);
		requestJson.put("birthDate", LocalDate.of(1981, 2, 13).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isBadRequest());
	}

	@Test
	public void TestSaveStudentWithNotValidArgument_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "");
		requestJson.put("birthDate", LocalDate.of(1981, 2, 13).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is4xxClientError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.message[0]").value("Name cannot be blank"));
	}

	@Test
	public void testDeleteStudent_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNoContent())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testDeleteStudentNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/student/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testGetStudentCourses_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1/courses")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].subject").value("Subject1"))
				.andExpect(jsonPath("$[0].code").value("CODE1"))
				.andExpect(jsonPath("$[0].description").value("Description1"));
	}

	@Test
	public void testGetStudentCoursesNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/99999/courses")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testGetGradesByStudentId_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1/grades")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].score").value("A"))
				.andExpect(jsonPath("$[0].course.subject").value("Subject1"))
				.andExpect(jsonPath("$[0].student.name").value("Harry Potter"));
	}

	@Test
	public void testGetGradesByStudentIdNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/99999/grades")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testUpdateStudent_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Lily Potter");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.put("/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Lily Potter"))
				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
	}

	@Test
	public void testUpdateStudentNotFound_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Lily Potter");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.put("/student/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testUpdateStudentNameBlank_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.put("/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testGetCourse_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.subject").value("Subject1"))
				.andExpect(jsonPath("$.code").value("CODE1"))
				.andExpect(jsonPath("$.description").value("Description1"));
	}

	@Test
	public void testGetCourseNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetCourses_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/all")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].subject").value("Subject1"))
				.andExpect(jsonPath("$[0].code").value("CODE1"))
				.andExpect(jsonPath("$[0].description").value("Description1"));
	}

	@Test
	public void testSaveCourse_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject4");
		requestJson.put("code", "CODE4");
		requestJson.put("description", "Description4");

		RequestBuilder request = MockMvcRequestBuilders.post("/course")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.subject").value("Subject4"))
				.andExpect(jsonPath("$.code").value("CODE4"))
				.andExpect(jsonPath("$.description").value("Description4"));
	}

	@Test
	public void testSaveCourseBadRequest_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", null);
		requestJson.put("code", "CODE5");
		requestJson.put("description", "Description5");

		RequestBuilder request = MockMvcRequestBuilders.post("/course")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testSaveCourseRepeatedCode_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject1");
		requestJson.put("code", "CODE1");
		requestJson.put("description", "Description1");

		RequestBuilder request = MockMvcRequestBuilders.post("/course")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testDeleteCourse_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/course/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNoContent())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testDeleteCourseNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/course/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testEnrollStudentToCourse_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/course/1/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testEnrollStudentToCourseNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/course/99999/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testEnrollStudentToCourseAlreadyEnrolled_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/course/1/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testUnenrollStudentFromCourse_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/course/1/student/1/unenroll")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testUnenrollStudentFromCourseNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/course/99999/student/1/unenroll")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testGetEnrolledStudents_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/1/students")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("Harry Potter"))
				.andExpect(jsonPath("$[0].birthDate").value("1980-07-31"));
	}

	@Test
	public void testGetEnrolledStudentsNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/99999/students")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testUpdateCourse_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject5");
		requestJson.put("code", "CODE5");
		requestJson.put("description", "Description5");

		RequestBuilder request = MockMvcRequestBuilders.put("/course/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.subject").value("Subject5"))
				.andExpect(jsonPath("$.code").value("CODE5"))
				.andExpect(jsonPath("$.description").value("Description5"));
	}

	@Test
	public void testUpdateCourseNotFound_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject5");
		requestJson.put("code", "CODE5");
		requestJson.put("description", "Description5");

		RequestBuilder request = MockMvcRequestBuilders.put("/course/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testUpdateCourseBadRequest_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "");
		requestJson.put("code", "CODE5");
		requestJson.put("description", "Description5");

		RequestBuilder request = MockMvcRequestBuilders.put("/course/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testUpdateCourseRepeatedCode_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject2");
		requestJson.put("code", "CODE2");
		requestJson.put("description", "Description2");

		RequestBuilder request = MockMvcRequestBuilders.put("/course/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testGetGrade_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/grade/course/1/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.score").value("A"))
				.andExpect(jsonPath("$.student.id").value("1"))
				.andExpect(jsonPath("$.student.name").value("Harry Potter"))
				.andExpect(jsonPath("$.student.birthDate").value("1980-07-31"))
				.andExpect(jsonPath("$.course.id").value("1"))
				.andExpect(jsonPath("$.course.subject").value("Subject1"))
				.andExpect(jsonPath("$.course.code").value("CODE1"))
				.andExpect(jsonPath("$.course.description").value("Description1"));
	}

	@Test
	public void testGetGradeDoesNotExist_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/grade/course/2/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testGetGrades_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/grade/all")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].score").value("A"))
				.andExpect(jsonPath("$[0].student.id").value("1"))
				.andExpect(jsonPath("$[0].student.name").value("Harry Potter"))
				.andExpect(jsonPath("$[0].student.birthDate").value("1980-07-31"))
				.andExpect(jsonPath("$[0].course.id").value("1"))
				.andExpect(jsonPath("$[0].course.subject").value("Subject1"))
				.andExpect(jsonPath("$[0].course.code").value("CODE1"))
				.andExpect(jsonPath("$[0].course.description").value("Description1"));
	}

	@Test
	public void testSaveGrade_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("score", "B");

		RequestBuilder request = MockMvcRequestBuilders.post("/grade/course/2/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.score").value("B"))
				.andExpect(jsonPath("$.student.id").value("3"))
				.andExpect(jsonPath("$.student.name").value("Neville Longbottom"))
				.andExpect(jsonPath("$.student.birthDate").value("1980-07-30"))
				.andExpect(jsonPath("$.course.id").value("2"))
				.andExpect(jsonPath("$.course.subject").value("Subject2"))
				.andExpect(jsonPath("$.course.code").value("CODE2"))
				.andExpect(jsonPath("$.course.description").value("Description2"));
	}

	@Test
	public void testSaveGradeScoreNull_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("score", null);

		RequestBuilder request = MockMvcRequestBuilders.post("/grade/course/2/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testSaveGradeNotFound_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("score", "A");

		RequestBuilder request = MockMvcRequestBuilders.post("/grade/course/99999/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testSaveGradeStudentNotEnrolled_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("score", "B");

		RequestBuilder request = MockMvcRequestBuilders.post("/grade/course/1/student/3")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testDeleteGrade_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/grade/course/1/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNoContent())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testDeleteGradeNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/grade/course/99999/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testGetCourseGrades_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/grade/course/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].score").value("A"))
				.andExpect(jsonPath("$[0].student.id").value("1"))
				.andExpect(jsonPath("$[0].student.name").value("Harry Potter"))
				.andExpect(jsonPath("$[0].student.birthDate").value("1980-07-31"))
				.andExpect(jsonPath("$[0].course.id").value("1"))
				.andExpect(jsonPath("$[0].course.subject").value("Subject1"))
				.andExpect(jsonPath("$[0].course.code").value("CODE1"))
				.andExpect(jsonPath("$[0].course.description").value("Description1"));
	}

	@Test
	public void testGetCourseGradesNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/grade/course/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testCreateUser_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("username", "User test");
		requestJson.put("password", "passwordtest");

		RequestBuilder request = MockMvcRequestBuilders.post("/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isCreated())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testCreateUserRepeatedUsername_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("username", "User");
		requestJson.put("password", "passwordtest");

		RequestBuilder request = MockMvcRequestBuilders.post("/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.message[0]").value(containsString("Username already exists")));
	}

	@Test
	public void testGetUser_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
				.andExpect(content().string(not(emptyString())))
				.andExpect(content().string("User"));
	}

	@Test
	public void testGetUserNotFound_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/99999")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	public void testAuthenticateBadJson_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("user", user.getUsername());
		requestJson.put("password", user.getPassword());

		RequestBuilder request = MockMvcRequestBuilders.post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().string(not(emptyString())))
				.andExpect(content().string("BAD REQUEST"));
	}

	@Test
	public void testAuthenticateUserDoesNotExist_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("username", "Invalid username");
		requestJson.put("password", user.getPassword());

		RequestBuilder request = MockMvcRequestBuilders.post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isNotFound())
				.andExpect(content().string(not(emptyString())))
				.andExpect(content().string("Username doesn't exist"));
	}

	@Test
	public void testAuthorizationInvalidToken_Fail() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1")
				.header(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + invalidToken);

		mockMvc.perform(request)
				.andExpect(status().isForbidden())
				.andExpect(content().string(not(emptyString())));
	}

}
