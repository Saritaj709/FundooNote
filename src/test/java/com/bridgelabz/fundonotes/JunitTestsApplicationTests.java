package com.bridgelabz.fundonotes;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FundooNotesApplication.class)
public class JunitTestsApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private ObjectMapper mapper = new ObjectMapper();

	private Resource casesFile;

	private Map<String, Json> cases;

	@Before
	public void setup() throws JsonParseException, JsonMappingException, IOException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		casesFile = new ClassPathResource("cases.json");
		cases = mapper.readValue(casesFile.getInputStream(), new TypeReference<Map<String, Json>>() {
		});
	}
	
	@Test
	public void test1() throws Exception {
		Json json = cases.get("TC-001");
		test(json);
	}
	@Test
	public void test2() throws Exception {
		Json json = cases.get("TC-002");
		test(json);
	}
	@Test
	public void test3() throws Exception {
		Json json = cases.get("TC-003");
		test(json);
	}
	@Test
	public void test4() throws Exception {
		Json json = cases.get("TC-004");
		test(json);
	}
	@Test
	public void test5() throws Exception {
		Json json = cases.get("TC-005");
		test(json);
	}
	@Test
	public void test6() throws Exception {
		Json json = cases.get("TC-006");
		test(json);
	}
	@Test
	public void test7() throws Exception {
		Json json = cases.get("TC-007");
		test(json);
	}
	@Test
	public void test8() throws Exception {
		Json json = cases.get("TC-008");
		test(json);
	}
	@Test
	public void test9() throws Exception {
		Json json = cases.get("TC-009");
		test(json);
	}
	@Test
	public void test10() throws Exception {
		Json json = cases.get("TC-010");
		test(json);
	}
	@Test
	public void test11() throws Exception {
		Json json = cases.get("TC-011");
		test(json);
	}
	@Test
	public void test12() throws Exception {
		Json json = cases.get("TC-012");
		test(json);
	}
	@Test
	public void test13() throws Exception {
		Json json = cases.get("TC-013");
		test(json);
	}
	
	@Test
	public void test14() throws Exception {
		Json json = cases.get("TC-014");
		test(json);
	}

	@Test
	public void test15() throws Exception {
		Json json = cases.get("TC-015");
		test(json);
	}

	@Test
	public void test16() throws Exception {
		Json json = cases.get("TC-016");
		test(json);
	}
	@Test
	public void test17() throws Exception {
		Json json = cases.get("TC-017");
		test(json);
	}
	@Test
	public void test18() throws Exception {
		Json json = cases.get("TC-018");
		test(json);
	}
	@Test
	public void test19() throws Exception {
		Json json = cases.get("TC-019");
		test(json);
	}
	@Test
	public void test20() throws Exception {
		Json json = cases.get("TC-020");
		test(json);
	}
	@Test
	public void test21() throws Exception {
		Json json = cases.get("TC-021");
		test(json);
	}
	@Test
	public void test22() throws Exception {
		Json json = cases.get("TC-022");
		test(json);
	}
	@Test
	public void test23() throws Exception {
		Json json = cases.get("TC-023");
		test(json);
	}
	@Test
	public void test24() throws Exception {
		Json json = cases.get("TC-024");
		test(json);
	}
	@Test
	public void test25() throws Exception {
		Json json = cases.get("TC-025");
		test(json);
	}
	@Test
	public void test26() throws Exception {
		Json json = cases.get("TC-026");
		test(json);
	}
	@Test
	public void test27() throws Exception {
		Json json = cases.get("TC-027");
		test(json);
	}
	@Test
	public void test28() throws Exception {
		Json json = cases.get("TC-028");
		test(json);
	}
	
	/*@Test
	public void test2() throws Exception {
		Json json = cases.get("demo2");
		test(json);
	}*/

	private void test(Json json) throws Exception {
		ResultActions actions = mockMvc
				.perform(getMethod(json).headers(json.getRequest().getHeaders()).contentType(MediaType.APPLICATION_JSON)
						.content(getRequestBody(json)).accept(MediaType.APPLICATION_JSON));

		actions.andExpect(status().is(json.getResponse().getStatus().value()));

		MockHttpServletResponse response = actions.andReturn().getResponse();

		for (String key : json.getResponse().getHeaders().keySet()) {
			assertEquals(json.getResponse().getHeaders().get(key), response.getHeader(key));
		}
		assertEquals(getResponseBody(json), response.getContentAsString());
	}

	private MockHttpServletRequestBuilder getMethod(Json json) {
		return MockMvcRequestBuilders.request(HttpMethod.resolve(json.getRequest().getMethod()),
				json.getRequest().getUrl());
	}

	private String getRequestBody(Json json) throws JsonProcessingException {
		return mapper.writeValueAsString(json.getRequest().getBody());
	}

	private String getResponseBody(Json json) throws JsonProcessingException {
		return mapper.writeValueAsString(json.getResponse().getBody());
	}

}
