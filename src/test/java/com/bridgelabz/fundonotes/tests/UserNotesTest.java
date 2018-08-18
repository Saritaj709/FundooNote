package com.bridgelabz.fundonotes.tests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.bridgelabz.fundonotes.FundooNotesApplication;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=FundooNotesApplication.class)
@SpringBootTest
public class UserNotesTest{

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

	}
	
	/*********************************************************************************************************/
	      /*****Tests For User****************/
	
	//@Test
	public void userRegisterTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON).content(
				"{\"email\":\"shrutilaxetti@gmail.com\",\"firstname\":\"shruti\",\"lastname\":\"laxetti\",\"phoneNo\":\"7459654696\",\"password\":\"shruti\",\"confirmPassword\":\"shruti\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("User registered successfully"))
				.andExpect(jsonPath("$.status").value(1));
	}

	//@Test
	public void loginTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/login").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"email\" :\"shrutilaxetti@gmail.com\",\"password\":\"shruti\"}")
				.accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.message").value("User Sucessfully logged in"))
				.andExpect(jsonPath("$.status").value(2));
	}

	 //@Test
	public void activateTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/activate").param("token",
				"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk")
				.content(MediaType.TEXT_PLAIN_VALUE))
		        .andExpect(jsonPath("$.message").value("Account activated successfully"))
				.andExpect(jsonPath("$.status").value(1));
	}

	//@Test
	public void forgetPasswordTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/user/forgotpassword").param("id", "5b559aee1cace90f51408ac8")
				.content(MediaType.TEXT_PLAIN_VALUE))
		        .andExpect(jsonPath("$.message").value("link sent to email,pls check and verify"))
				.andExpect(jsonPath("$.status").value(1));
	}

	// @Test
	public void resetPasswordTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/user/resetpassword").contentType(MediaType.APPLICATION_JSON).param(
				"token",
				"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTE1MjIsInN1YiI6IjViNTU5YWVlMWNhY2U5MGY1MTQwOGFjOCJ9.EXPuTNFKa6SICBjS7lOAoQjjxrFZ14_YavT2-bu6CQA")
				.content("{ \"password\" :\"Shru@1234\",\"confirmPassword\":\"Shru@1234\"}"))
				.andExpect(jsonPath("$.message").value("Password is successfully changed"))
				.andExpect(jsonPath("$.status").value(4));
	}
	
	
	
	/*********************************************************************************************/
	        /**Tests for NoteController**/
	
	//@Test
	public void noteRegisterTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/create").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk").content(
				"{\"title\":\"note for test\",\"description\":\"note green\",\"userId\":\"5b55ac911cace92d7a53dd61\",\"label\":\"good\",\"color\":\"blue\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("your note is successfully created"))
				.andExpect(jsonPath("$.status").value(1));
	}
	
//	@Test
		public void noteUpdateTest() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.put("/api/notes/update").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk").content(
					"{\"title\":\"note for mockito test\",\"description\":\"note green\",\"noteId\":\"5b55c8e61cace95939105613\",\"label\":\"good\"}")
					.accept(MediaType.TEXT_PLAIN_VALUE))
					.andExpect(jsonPath("$.message").value("Note successfully updated"))
					.andExpect(jsonPath("$.status").value(2));
		}
		
		//@Test
				public void noteDeleteTest() throws Exception {
					mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/delete/{noteId}","5b55c8e61cace95939105613").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk")
							.accept(MediaType.TEXT_PLAIN_VALUE))
							.andExpect(jsonPath("$.message").value("Congratulations, your note is successfully moved to trash"))
							.andExpect(jsonPath("$.status").value(3));
				}
	
	   //	@Test
		public void noteRestoreTest() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/restore/{noteId}","5b55c8e61cace95939105613").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk")
					.accept(MediaType.TEXT_PLAIN_VALUE))
					.andExpect(jsonPath("$.message").value("Congratulations, your note is successfully restored"))
					.andExpect(jsonPath("$.status").value(4));
		}
		
		//@Test
		public void noteAddReminderTest() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/addReminder/{noteId}","5b55c8e61cace95939105613").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk").param("date","2019/06/06")
					.accept(MediaType.TEXT_PLAIN_VALUE))
					.andExpect(jsonPath("$.message").value("Congratulations,your reminder is successfully set!!"))
					.andExpect(jsonPath("$.status").value(7));
		}
		
		
		
		//@Test
				public void noteRemoveReminderTest() throws Exception {
					mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/deleteReminder/{noteId}","5b55c8e61cace95939105613").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk")
							.accept(MediaType.TEXT_PLAIN_VALUE))
							.andExpect(jsonPath("$.message").value("Congratulations,your reminder is successfully removed!!"))
							.andExpect(jsonPath("$.status").value(9));
				}

				//@Test
				public void noteDeleteForeverTest() throws Exception {
					mockMvc.perform(MockMvcRequestBuilders.delete("/api/notes/deleteForever/{noteId}","5b55c8e61cace95939105613").contentType(MediaType.APPLICATION_JSON).header("token","eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJ2ZXJpZnkiLCJpYXQiOjE1MzI0MTMzOTMsInN1YiI6IjViNTVhYzkxMWNhY2U5MmQ3YTUzZGQ2MSJ9.iKwYXsbaFz791y-9QtlrZI_3ew_vhoHxqarZYKbWPVk")
							.accept(MediaType.TEXT_PLAIN_VALUE))
							.andExpect(jsonPath("$.message").value("Congratulations,your details are successfully deleted"))
							.andExpect(jsonPath("$.status").value(4));
				}

}
