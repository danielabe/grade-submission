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
class GradeSubmissionApplicationTestsUser {

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
	public void testCreateUser_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("username", user.getUsername());
		requestJson.put("password", user.getPassword());

		RequestBuilder request = MockMvcRequestBuilders.post("/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(emptyString()));
	}

	@Test
	@Order(3)
	public void testGetUser_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/1")
				.header("Authorization", SecurityConstants.BEARER + token);

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
				.andExpect(content().string(not(emptyString())))
				.andExpect(content().string("User"));
	}

}
