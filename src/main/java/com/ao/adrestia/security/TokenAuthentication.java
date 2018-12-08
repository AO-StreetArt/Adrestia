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

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class TokenAuthentication extends AbstractAuthenticationToken {

  private static final Logger logger = LoggerFactory.getLogger("adrestia.AuthController");

  private final DecodedJWT jwt;
  private boolean invalidated;

  public TokenAuthentication(DecodedJWT jwt) {
    super(readAuthorities(jwt));
    this.jwt = jwt;
  }

  private boolean hasExpired() {
    return jwt.getExpiresAt().before(new Date());
  }

  private static Collection<? extends GrantedAuthority> readAuthorities(DecodedJWT jwt) {
    Claim rolesClaim = jwt.getClaim("https://access.control/roles");
    if (rolesClaim.isNull()) {
      logger.warn("No Roles found for JWT Token");
      return Collections.emptyList();
    }
    List<GrantedAuthority> authorities = new ArrayList<>();
    String[] scopes = rolesClaim.asArray(String.class);
    logger.info("Role Claim Scopes: {}", scopes);
    for (String s : scopes) {
      SimpleGrantedAuthority a = new SimpleGrantedAuthority(s);
      if (!authorities.contains(a)) {
        logger.info("Granting Authority: {}", a.toString());
        authorities.add(a);
      }
    }
    return authorities;
  }


  @Override
  public String getCredentials() {
    return jwt.getToken();
  }

  @Override
  public Object getPrincipal() {
    return jwt.getSubject();
  }

  @Override
  public void setAuthenticated(boolean authenticated) {
    logger.info("Invalidating Authentication Token");
    if (authenticated) {
      throw new IllegalArgumentException("Create a new Authentication object to authenticate");
    }
    invalidated = true;
  }

  @Override
  public boolean isAuthenticated() {
    return !invalidated && !hasExpired();
  }
}
