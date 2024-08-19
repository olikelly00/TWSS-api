package tech.makers.aceplay.storage;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=2362s
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StorageControllerIntegrationTest {
  @Autowired private MockMvc mvc;

  @Test
  @WithMockUser
  void WhenLoggedIn_AndGivenWrongType_GetTrackUploadKeyReturnsClientError() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/storage/upload_key")
                .param("filename", "wow.txt")
                .param("contentType", "text/plain"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @WithMockUser
  void WhenLoggedIn_AndGivenAudioType_GetTrackUploadKeyGetsKey() throws Exception {
    mvc.perform(
            MockMvcRequestBuilders.get("/api/storage/upload_key")
                .param("filename", "wow.mp3")
                .param("contentType", "audio/mpeg"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.publicUrl").value(Matchers.stringContainsInOrder("wow", ".mp3")))
        .andExpect(
            jsonPath("$.signedUploadUrl").value(Matchers.stringContainsInOrder("wow", ".mp3")));
  }

  @Test
  void WhenLoggedOut_GetTrackUploadKeyIsForbidden() throws Exception {
    mvc.perform(MockMvcRequestBuilders.post("/api/storage/upload_key"))
        .andExpect(status().isForbidden());
  }
}
