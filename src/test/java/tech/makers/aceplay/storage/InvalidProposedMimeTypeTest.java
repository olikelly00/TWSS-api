package tech.makers.aceplay.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=2034s
class InvalidProposedMimeTypeTest {

  @Test
  void testGetMessage() {
    InvalidProposedMimeType subject = new InvalidProposedMimeType("text/plain", "message");
    assertEquals("Invalid mime type \"text/plain\": message", subject.getMessage());
  }
}
