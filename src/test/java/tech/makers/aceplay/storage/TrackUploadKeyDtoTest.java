package tech.makers.aceplay.storage;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=2003s
class TrackUploadKeyDtoTest {
  @Test
  void testConstructs() throws MalformedURLException {
    TrackUploadKeyDto subject =
        new TrackUploadKeyDto(
            new URL("http://example.org/public_url"),
            new URL("http://example.com/signed_upload_url"));

    assertEquals(new URL("http://example.org/public_url"), subject.getPublicUrl());
    assertEquals(new URL("http://example.com/signed_upload_url"), subject.getSignedUploadUrl());
  }
}
