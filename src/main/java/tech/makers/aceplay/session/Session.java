package tech.makers.aceplay.session;

import tech.makers.aceplay.user.User;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=2364s
public class Session {
  private User user;
  private String token;

  public Session(User user, String token) {
    this.setUser(user);
    this.setToken(token);
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
