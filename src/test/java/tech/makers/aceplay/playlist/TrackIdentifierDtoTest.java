package tech.makers.aceplay.playlist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=1139s
class TrackIdentifierDtoTest {
  @Test
  void testConstructs() {
    TrackIdentifierDto subject = new TrackIdentifierDto(5L);
    assertEquals(5L, subject.getId());
  }
}
