/*
Apache2 License Notice
Copyright 2017 Alex Barry

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

package com.ao.adrestia.controller;

import com.ao.adrestia.model.ApplicationUser;
import com.ao.adrestia.repo.ApplicationUserRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/users")
public class UsersController {
  private static Logger log = LoggerFactory.getLogger("adrestia.UserController");

  @Autowired
  ApplicationUserRepository applicationUserRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(@RequestBody ApplicationUser user) {
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    String responseBody = "";
    // Detect any existing users
    List<ApplicationUser> existingUsers = applicationUserRepository.findByUsername(user.username);
    if (existingUsers.size() > 0) {
      log.info("Sign-up requested for existing user");
      returnCode = HttpStatus.CONFLICT;
    } else {
      log.info("Signing up new User: {}", user.username);
      user.password = bCryptPasswordEncoder.encode(user.getPassword());
      applicationUserRepository.save(user);
      returnCode = HttpStatus.OK;
    }
    return new ResponseEntity<String>(responseBody, responseHeaders, returnCode);
  }
}
