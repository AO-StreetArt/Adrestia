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

import com.auth0.jwt.JWT;
import com.ao.adrestia.model.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private static Logger log = LoggerFactory.getLogger("adrestia.JWTAuthenticationFilter");
  private AuthenticationManager authenticationManager;
  private String SECRET;
  private static final long EXPIRATION_TIME = 86_400_000; // 1 day expiration
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
  public static final String SIGN_UP_URL = "/users/sign-up";

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String secret) {
    this.authenticationManager = authenticationManager;
    this.SECRET = secret;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req,
      HttpServletResponse res) throws AuthenticationException {
    try {
      ApplicationUser creds = new ObjectMapper()
          .readValue(req.getInputStream(), ApplicationUser.class);

      log.debug("Attempting Authentication for user {}", creds.username);

      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              creds.username,
              creds.password,
              new ArrayList<>()
          )
      );
    } catch (IOException e) {
      log.error("Exception encountered during Authentication", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res,
      FilterChain chain,
      Authentication auth) throws IOException, ServletException {
    String token = JWT.create()
        .withSubject(((User) auth.getPrincipal()).getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + this.EXPIRATION_TIME))
        .sign(HMAC512(this.SECRET.getBytes(StandardCharsets.UTF_8)));
    log.debug("Authentication Successful");
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    res.addCookie(new Cookie("access_token", token));
    //res.addHeader("Set-Cookie", "access_token=" + token);
  }
}
