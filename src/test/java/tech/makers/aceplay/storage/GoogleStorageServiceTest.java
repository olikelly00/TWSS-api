package tech.makers.aceplay.storage;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=2075s
@SpringBootTest
class GoogleStorageServiceTest {

  @Autowired private GoogleStorageService subject;

  @Test
  void testMakeSignedUploadUrl_withValidContentType() throws IOException, InvalidProposedMimeType {
    URL result = subject.makeSignedUploadUrl("filename.mp3", "audio/mpeg");
    assertThat(
        result.toString(),
        Matchers.startsWith("https://storage.googleapis.com/fake-bucket-name/filename.mp3?"));
  }

  @Test
  void testMakeSignedUploadUrl_withInvalidContentType() {
    assertThrows(
        InvalidProposedMimeType.class,
        () -> subject.makeSignedUploadUrl("filename.mp3", "text/plain"));
  }

  @Test
  void testGetPublicUrl() throws MalformedURLException {
    URL result = subject.getPublicUrl("filename.mp3");
    assertEquals(new URL("https://storage.googleapis.com/fake-bucket-name/filename.mp3"), result);
  }
}
