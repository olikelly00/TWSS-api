package tech.makers.aceplay.session;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=1456s
class SessionSecretTest {
  @AfterEach
  void resetReflection() {
    ReflectionTestUtils.setField(SessionSecret.class, "activeProfile", "test");
    ReflectionTestUtils.setField(SessionSecret.class, "secretKeyBase64Encoded", null);
  }

  @Test
  void testGetKeyGeneratesKey() {
    SecretKey secretKey = SessionSecret.getKey();
    assertNotNull(secretKey);
  }

  @Test
  void testGetKeyGeneratesSameKeyTwice() {
    SecretKey secretKey1 = SessionSecret.getKey();
    SecretKey secretKey2 = SessionSecret.getKey();
    assertEquals(secretKey1, secretKey2);
  }

  @Test
  void testGetKeyRefusesToGenerateKeyInProd() {
    ReflectionTestUtils.setField(SessionSecret.class, "activeProfile", "prod");
    assertThrows(SessionSecretNotProvidedException.class, SessionSecret::getKey);
  }

  @Test
  void testGetKeyUsesKeyInProd() {
    String testSecretKey = "QtMqoRQ9cZdD6msX+6aOUJJjGW9IczogExFsTpQYBXA=";
    ReflectionTestUtils.setField(SessionSecret.class, "activeProfile", "prod");
    ReflectionTestUtils.setField(SessionSecret.class, "secretKeyBase64Encoded", testSecretKey);
    SecretKey key = SessionSecret.getKey();
    assertEquals(Encoders.BASE64.encode(key.getEncoded()), testSecretKey);
  }
}
