package tech.makers.aceplay.session;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.makers.aceplay.user.UserRepository;

import java.util.List;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=1799s
@Service
public class SessionService {
  public static final String TOKEN_PREFIX = "Bearer ";

  @Autowired private AuthenticationProvider authenticationManager;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  public String login(String username, String password) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
      return generateToken(username);
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  public String generateToken(String username) {
    List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

    String token =
        Jwts.builder()
            .setSubject(username)
            .claim("authorities", grantedAuthorities)
            .signWith(SessionSecret.getKey())
            .compact();

    return TOKEN_PREFIX + token;
  }
}
