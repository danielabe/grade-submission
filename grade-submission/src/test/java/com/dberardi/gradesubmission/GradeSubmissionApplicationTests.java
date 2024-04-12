package com.dberardi.gradesubmission;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GradeSubmissionApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
		assertNotNull(mockMvc);
	}

	@Test
	public void testGetStudent_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1");

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Harry Potter"))
				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
	}

	@Test
	public void testGetStudent_ExceptionHandling() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/99999");

		mockMvc.perform(request)
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetStudents() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/all");

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
	public void testSaveStudent_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Harry Potter");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
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
	public void testSaveStudent_Fail() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", null);
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.post("/student")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson.toString());

		mockMvc.perform(request)
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testUpdateStudent_Success() throws Exception {
		JSONObject requestJson = new JSONObject();
		requestJson.put("name", "Lily Potter");
		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());

		RequestBuilder request = MockMvcRequestBuilders.put("/student/1")
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
	public void testDeleteStudent_Success() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/student/1");

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(status().isNoContent())
				.andExpect(content().string(emptyString()));
	}

	@Test
	public void testGetEnrolledCourses() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1/courses");

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
	public void testGetGradesByStudentId() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/student/1/grades");

		mockMvc.perform(request)
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(not(emptyString())))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].score").value("A"))
				.andExpect(jsonPath("$[0].course.subject").value("Subject1"))
				.andExpect(jsonPath("$[0].student.name").value("Harry Potter"));
	}








//	@Test
//	public void testGetStudent_Success() throws Exception {
//		Student mockStudent = new Student(1L, "Harry Potter", LocalDate.of(1980, 7, 31), Collections.emptyList(), Collections.emptySet());
//		when(studentService.getStudent(1L)).thenReturn(mockStudent);
//
//		RequestBuilder request = MockMvcRequestBuilders.get("/student/1");
//
//		mockMvc.perform(request)
//				.andExpect(status().is2xxSuccessful())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(content().string(not(emptyString())))
//				.andExpect(jsonPath("$.id").value(1))
//				.andExpect(jsonPath("$.name").value("Harry Potter"))
//				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
//
//	}
//
//	@Test
//	public void testGetStudent_ExceptionHandling() throws Exception {
//		when(studentService.getStudent(99999L)).thenThrow(new StudentNotFoundException(99999L));
//
//		RequestBuilder request = MockMvcRequestBuilders.get("/student/99999");
//
//		mockMvc.perform(request)
//				.andExpect(status().isNotFound());
//	}
//
//	@Test
//	public void testGetStudents() throws Exception {
//		Student student = new Student(1L, "Harry Potter", LocalDate.of(1980, 7, 31), Collections.emptyList(), Collections.emptySet());
//		List<Student> mockStudents = Arrays.asList(student);
//		when(studentService.getStudents()).thenReturn(mockStudents);
//
//		RequestBuilder request = MockMvcRequestBuilders.get("/student/all");
//
//		mockMvc.perform(request)
//				.andExpect(status().is2xxSuccessful())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(content().string(not(emptyString())))
//				.andExpect(jsonPath("$", hasSize(1)))
//				.andExpect(jsonPath("$[0].id").value(1))
//				.andExpect(jsonPath("$[0].name").value("Harry Potter"))
//				.andExpect(jsonPath("$[0].birthDate").value("1980-07-31"));
//
//	}
//
//	@Test
//	public void testGetStudents_EmptyList() throws Exception {
//		when(studentService.getStudents()).thenReturn(Collections.emptyList());
//
//		RequestBuilder request = MockMvcRequestBuilders.get("/student/all");
//
//		mockMvc.perform(request)
//				.andExpect(status().is2xxSuccessful())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(content().string(not(emptyString())))
//				.andExpect(jsonPath("$", hasSize(0)));
//	}
//
//	@Test
//	public void testSaveStudent_Success() throws Exception {
//		when(studentService.saveStudent(any(Student.class)))
//				.thenReturn(new Student(1L, "Harry Potter", LocalDate.of(1980, 07, 31), Collections.emptyList(), Collections.emptySet()));
//
//		JSONObject requestJson = new JSONObject();
//		requestJson.put("name", "Harry Potter");
//		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());
//
//		RequestBuilder request = MockMvcRequestBuilders.post("/student")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(requestJson.toString());
//
//		MvcResult result = mockMvc.perform(request)
//				.andExpect(status().isCreated())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(content().string(not(emptyString())))
//				.andExpect(jsonPath("$.id").value(1))
//				.andExpect(jsonPath("$.name").value("Harry Potter"))
//				.andExpect(jsonPath("$.birthDate").value("1980-07-31"))
//				.andReturn();
//
//		MockHttpServletResponse response = result.getResponse();
//		String responseBody = response.getContentAsString();
//		System.out.println("Response Body: " + responseBody);
//	}
//
//	@Test
//	public void testSaveStudent_Fail() throws Exception {
//		JSONObject requestJson = new JSONObject();
//		requestJson.put("name", null);
//		requestJson.put("birthDate", LocalDate.of(1980, 7, 31).toString());
//
//		RequestBuilder request = MockMvcRequestBuilders.post("/student")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(requestJson.toString());
//
//		mockMvc.perform(request)
//				.andExpect(status().is4xxClientError());
//	}
//
//	@Test
//	public void testUpdateStudent_Success() throws Exception {
//		Student student = new Student(1L, "Harry Potter", LocalDate.of(1980, 7, 31), Collections.emptyList(), Collections.emptySet());
//		when(studentService.updateStudent(any(Student.class), any(Long.class))).thenReturn(student);
//
//		JSONObject requestJson = new JSONObject();
//		requestJson.put("name", "Lily Potter");
//
//		RequestBuilder request = MockMvcRequestBuilders.put("/student/1")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(requestJson.toString());
//
//		mockMvc.perform(request)
//				.andExpect(status().is2xxSuccessful())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(content().string(not(emptyString())))
//				.andExpect(jsonPath("$.id").value(1));
////				.andExpect(jsonPath("$.name").value("Lily Potter"));
////				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
//
//	}
//
//	@Test
//	public void testDeleteStudent_Success() throws Exception {
//		Student mockStudent = new Student(1L, "Harry Potter", LocalDate.of(1980, 7, 31), Collections.emptyList(), Collections.emptySet());
//		when(studentService.deleteStudent(1L)).thenReturn(ResponseEntity.noContent().build());
//
//		RequestBuilder request = MockMvcRequestBuilders.delete("/student/1");
//
//		mockMvc.perform(request)
//				.andExpect(status().is2xxSuccessful())
//				.andExpect(status().isNoContent())
////				.andExpect()
//				.andExpect(content().string(not(emptyString())));
////				.andExpect(jsonPath("$.id").value(1))
////				.andExpect(jsonPath("$.name").value("Harry Potter"))
////				.andExpect(jsonPath("$.birthDate").value("1980-07-31"));
//	}

}