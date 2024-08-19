package tech.makers.aceplay.user;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=141s
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UsersControllerIntegrationTests {
  @Autowired private MockMvc mvc;

  @Autowired private UserRepository repository;

  @Test
  void testUsersPostCreatesNewUser() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"kay\", \"password\": \"pass\"}"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user.username").value("kay"));
    assertNotNull(repository.findByUsername("kay"));
  }

  @Test
  void testUsersPostLogsUserIn() throws Exception {
    MvcResult result =
        mvc.perform(
                MockMvcRequestBuilders.post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\": \"kay\", \"password\": \"pass\"}"))
            .andExpect(status().isOk())
            .andReturn();

    String response = result.getResponse().getContentAsString();
    String token = JsonPath.parse(response).read("$.token");

    // Check if we can GET /api/session to prove we're logged in
    mvc.perform(MockMvcRequestBuilders.get("/api/session").header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.user.username").value("kay"));
  }
}
