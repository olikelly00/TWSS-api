package tech.makers.aceplay;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.makers.aceplay.session.SessionSecret;
import tech.makers.aceplay.session.SessionService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=451s
public class TokenAuthorizationFilter extends OncePerRequestFilter {
  private static final String HEADER = "Authorization";
  private final JwtParser tokenParser =
      Jwts.parserBuilder().setSigningKey(SessionSecret.getKey()).build();
  private final List<GrantedAuthority> defaultGrantedAuthorities =
      AuthorityUtils.createAuthorityList("ROLE_USER");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    try {
      extractClaimsFromToken(request).ifPresent(this::setUpSpringAuthentication);
      chain.doFilter(request, response);
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }
  }

  private Optional<Claims> extractClaimsFromToken(HttpServletRequest request) {
    if (!requestHasToken(request)) { return Optional.empty(); }
    String jwtToken = request.getHeader(HEADER).replace(SessionService.TOKEN_PREFIX, "");
    Claims claims = tokenParser.parseClaimsJws(jwtToken).getBody();
    if (claims.get("authorities") == null) { return Optional.empty(); }
    return Optional.of(claims);
  }

  private boolean requestHasToken(HttpServletRequest request) {
    String authenticationHeader = request.getHeader(HEADER);
    if (authenticationHeader == null) { return false; }
    return authenticationHeader.startsWith(SessionService.TOKEN_PREFIX);
  }

  private void setUpSpringAuthentication(Claims claims) {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, defaultGrantedAuthorities);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
