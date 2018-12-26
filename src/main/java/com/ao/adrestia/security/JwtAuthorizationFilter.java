/*
Apache2 License Notice
Copyright 2018 Alex Barry
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ao.adrestia.security;

import com.ao.adrestia.model.ApplicationUser;
import com.ao.adrestia.repo.ApplicationUserRepository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private static Logger log = LoggerFactory.getLogger("adrestia.JWTAuthorizationFilter");
  private String secret;
  private ApplicationUserRepository userRepository;
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";

  /**
  * Auth Filter Constructor.
  */
  public JwtAuthorizationFilter(AuthenticationManager authManager,
      ApplicationUserRepository userRepo, String secret) {
    super(authManager);
    this.secret = secret;
    this.userRepository = userRepo;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(HEADER_STRING);

    boolean accessCookieFound = false;

    // Look for an access_token cookie
    Cookie[] requestCookies = req.getCookies();
    if (requestCookies != null) {
      for (int i = 0; i < requestCookies.length; i++) {
        if (requestCookies[i].getName().equals("access_token")) {
          accessCookieFound = true;
        }
      }
    }

    // Look for an Authorization header
    if ((header == null || !header.startsWith(TOKEN_PREFIX)) && !accessCookieFound) {
      chain.doFilter(req, res);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token != null && token.contains(TOKEN_PREFIX)) {
      log.debug("Parsing JWT from Authorization Header");
      token = token.replace(TOKEN_PREFIX, "");
    } else {
      // Check for a cookie
      Cookie[] requestCookies = request.getCookies();
      for (int i = 0; i < requestCookies.length; i++) {
        if (requestCookies[i].getName().equals("access_token")) {
          log.debug("Parsing JWT from Cookie");
          token = requestCookies[i].getValue();
        }
      }
    }
    // parse the token.
    String user = JWT.require(Algorithm.HMAC512(
        this.secret.getBytes(StandardCharsets.UTF_8)))
        .build().verify(token).getSubject();

    if (user != null) {
      log.debug("User retrieved from JWT {}", user);
      log.debug("Validating Request URL: {}", request.getRequestURL().toString());
      // Validate user access
      List<ApplicationUser> requestUsers = this.userRepository.findByUsername(user);
      if (requestUsers.size() > 0) {
        // Admin validation
        if (!(requestUsers.get(0).getIsAdmin() || requestUsers.get(0).getIsActive())
            && (request.getRequestURI().contains("sign-up")
            || request.getRequestURI().contains("cluster")
            || request.getRequestURI().contains("cache"))) {
          log.warn("Rejecting access to core cluster endpoint for user {}", user);
          return null;
        }
        // Non-admin users can only acces user endpoints for themselves
        if (!(requestUsers.get(0).getIsAdmin())) {
          if (request.getRequestURI().contains("users")
              && !(request.getRequestURI().contains(requestUsers.get(0).getId())
              || request.getQueryString().contains(requestUsers.get(0).getUsername()))) {
            log.warn("Rejecting access to user endpoint for non-matching user {}", user);
            return null;
          }
        }
      } else {
        log.warn("Unable to find ApplicationUser matching presented token user {}", user);
        return null;
      }
      return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
    return null;
  }
}
