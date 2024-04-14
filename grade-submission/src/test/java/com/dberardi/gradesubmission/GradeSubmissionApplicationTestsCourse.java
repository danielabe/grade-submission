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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class GradeSubmissionApplicationTestsCourse {

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
	public void testGetCourse_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/1")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.subject").value("Subject1"))
				.andExpect(jsonPath("$.code").value("CODE1"))
				.andExpect(jsonPath("$.description").value("Description1"));
	}

	@Test
	@Order(3)
	public void testGetCourse_ExceptionHandling() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/99999")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	@Order(4)
	public void testGetCourses() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/all")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].subject").value("Subject1"))
				.andExpect(jsonPath("$[0].code").value("CODE1"))
				.andExpect(jsonPath("$[0].description").value("Description1"));
	}

	@Test
	@Order(5)
	public void testSaveCourse_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject4");
		requestJson.put("code", "CODE4");
		requestJson.put("description", "Description4");

		RequestBuilder request = MockMvcRequestBuilders.post("/course")
				.header("Authorization", SecurityConstants.BEARER + token)
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
	@Order(6)
	public void testSaveCourse_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", null);
		requestJson.put("code", "CODE5");
		requestJson.put("description", "Description5");

		RequestBuilder request = MockMvcRequestBuilders.post("/course")
				.header("Authorization", SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is4xxClientError());
	}

	@Test
	@Order(11)
	public void testUpdateCourse_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("subject", "Subject5");
		requestJson.put("code", "CODE5");
		requestJson.put("description", "Description5");

		RequestBuilder request = MockMvcRequestBuilders.put("/course/1")
				.header("Authorization", SecurityConstants.BEARER + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.subject").value("Subject5"))
				.andExpect(jsonPath("$.code").value("CODE5"))
				.andExpect(jsonPath("$.description").value("Description5"));
	}

	@Test
	@Order(8)
	public void testDeleteCourse_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/course/4")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isNoContent())
				.andExpect(content().string(emptyString()));
	}

	@Test
	@Order(9)
	public void testEnrollStudentToCourse() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.put("/course/2/student/3")
				.header("Authorization", SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())));
	}

	@Test
	@Order(10)
	public void testGetEnrolledStudents() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/course/1/students")
				.header("Authorization", SecurityConstants.BEARER + token);;

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("Harry Potter"))
				.andExpect(jsonPath("$[0].birthDate").value("1980-07-31"));
	}

}