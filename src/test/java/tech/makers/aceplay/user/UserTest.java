package tech.makers.aceplay.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// https://www.youtube.com/watch?v=L4vkcgRnw2g&t=90s
class UserTest {
  @Test
  void testConstructs() {
    User subject = new User("username", "password");
    assertNull(subject.getId());
    assertEquals("username", subject.getUsername());
    assertEquals("password", subject.getPassword());
    assertTrue(subject.isAccountNonExpired());
    assertTrue(subject.isAccountNonLocked());
    assertTrue(subject.isCredentialsNonExpired());
    assertTrue(subject.isEnabled());
  }

  @Test
  void testToString() {
    User subject = new User("username", "password");
    assertEquals("User[id=null username='username' password=HIDDEN]", subject.toString());
  }

  @Test
  void testGetAuthorities() {
    User subject = new User("username", "password");
    assertEquals(1, subject.getAuthorities().size());
    assertEquals("ROLE_USER", subject.getAuthorities().iterator().next().getAuthority());
  }
}
