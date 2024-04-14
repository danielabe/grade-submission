package com.dberardi.gradesubmission;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dberardi.gradesubmission.model.User;
import com.dberardi.gradesubmission.security.SecurityConstants;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Date;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class GradeSubmissionApplicationTestsStudent {

	@Autowired
	private MockMvc mockMvc;

	User user = new User("User", "password123");
	String token = JWT.create()
			.withSubject(user.getUsername())
			.withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
			.sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

	@Test
	@Order(1)
	void contextLoads() {
		assertNotNull(mockMvc);
	}

	@Test
	@Order(2)
	public void testGetStudent_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Harry Potter"))
				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
	}

	@Test
	@Order(3)
	public void testGetStudent_ExceptionHandling() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/99999")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(4)
	public void testGetStudents() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/all")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("Harry Potter"))
				.andExpect(jsonPath("$[0].birthDate").value("1980-07-31"));
	}

	@Test
	@Order(5)
	public void testSaveStudent_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Harry Potter");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
				.header("Authorization", SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Harry Potter"))
				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
	}

	@Test
	@Order(6)
	public void testSaveStudent_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", null);
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
				.header("Authorization", SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is4xxClientError());
	}

	@Test
	@Order(10)
	public void testUpdateStudent_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Lily Potter");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.put("/student/1")
				.header("Authorization", SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Lily Potter"))
				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
	}

	@Test
	@Order(8)
	public void testDeleteStudent_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/student/4")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isNoContent())
				.andExpect(content().string(emptyString()));
	}

	@Test
	@Order(9)
	public void testGetEnrolledCourses() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1/courses")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].subject").value("Subject1"))
				.andExpect(jsonPath("$[0].code").value("CODE1"))
				.andExpect(jsonPath("$[0].description").value("Description1"));
	}

	@Test
	@Order(7)
	public void testGetGradesByStudentId() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1/grades")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].score").value("A"))
				.andExpect(jsonPath("$[0].course.subject").value("Subject1"))
				.andExpect(jsonPath("$[0].student.name").value("Harry Potter"));
	}

}