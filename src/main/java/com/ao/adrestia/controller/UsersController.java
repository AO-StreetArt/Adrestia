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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
* Rest Controller which provides CRUD operations for Users.
*/
@RestController
@RequestMapping("users")
public class UsersController {
  private static Logger log = LoggerFactory.getLogger("adrestia.UserController");

  @Autowired
  ApplicationUserRepository applicationUserRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
  * Sign-up a new user.
  */
  @PostMapping("/sign-up")
  public ResponseEntity<ApplicationUser> signUp(@RequestBody ApplicationUser user) {
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    // Detect any existing users
    List<ApplicationUser> existingUsers = applicationUserRepository.findByUsername(user.username);
    if (existingUsers.size() > 0) {
      log.warn("Sign-up requested for existing user");
      returnCode = HttpStatus.CONFLICT;
    } else {
      // Check for any users with the same email
      List<ApplicationUser> sameEmailUsers = applicationUserRepository.findByEmail(user.email);
      if (sameEmailUsers.size() > 0) {
        log.warn("Sign-up requested, existing email registration found");
        returnCode = HttpStatus.CONFLICT;
      } else {
        // Save the new user in the DB
        log.info("Signing up new User: {}", user.username);
        user.password = bCryptPasswordEncoder.encode(user.getPassword());
        applicationUserRepository.save(user);
      }
    }
    String responseBody = "";
    HttpHeaders responseHeaders = new HttpHeaders();
    user.password = "";
    return new ResponseEntity<ApplicationUser>(user, responseHeaders, returnCode);
  }

  /**
  * Update an existing user.
  */
  @PutMapping("/{key}")
  public ResponseEntity<ApplicationUser> updateUser(
      @RequestBody ApplicationUser user,
      @PathVariable("key") String key) {
    user.setId(key);
    user.password = bCryptPasswordEncoder.encode(user.getPassword());
    applicationUserRepository.save(user);
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    user.password = "";
    return new ResponseEntity<ApplicationUser>(user, responseHeaders, returnCode);
  }

  /**
  * Get a user by ID.
  */
  @GetMapping("/{key}")
  public ResponseEntity<ApplicationUser> getUser(@PathVariable("key") String key) {
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    ApplicationUser returnUser = new ApplicationUser();
    // Find any existing users
    Optional<ApplicationUser> existingUser = applicationUserRepository.findById(key);
    if (existingUser.isPresent()) {
      log.info("Retrieved user by ID");
      returnUser = existingUser.get();
    } else {
      log.warn("Unable to find User: {}", key);
      returnCode = HttpStatus.NOT_FOUND;
    }
    // Return the response
    HttpHeaders responseHeaders = new HttpHeaders();
    returnUser.password = "";
    return new ResponseEntity<ApplicationUser>(returnUser, responseHeaders, returnCode);
  }

  /**
  * Find users by username or email.
  */
  @GetMapping("/")
  public ResponseEntity<List<ApplicationUser>> findUser(
      @RequestParam(value = "username", defaultValue = "") String username,
      @RequestParam(value = "email", defaultValue = "") String email,
      @RequestParam(value = "num_records", defaultValue = "10") int recordsInPage,
      @RequestParam(value = "page", defaultValue = "0") int pageNum) {
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    ApplicationUser returnUser = new ApplicationUser();
    // Find any existing users
    List<ApplicationUser> existingUsers;
    if (!(username.isEmpty())) {
      existingUsers = applicationUserRepository.findByUsername(username);
    } else if (!(email.isEmpty())) {
      existingUsers = applicationUserRepository.findByEmail(email);
    } else {
      Pageable pageable = new PageRequest(pageNum, recordsInPage);
      Page<ApplicationUser> page = applicationUserRepository.findAll(pageable);
      existingUsers = page.getContent();
    }
    if (existingUsers.size() > 0) {
      log.info("Retrieved users");
    } else {
      log.warn("Unable to find Users: {}:{}", username, email);
      returnCode = HttpStatus.NOT_FOUND;
    }
    // Return the response
    returnUser.password = "";
    return new ResponseEntity<List<ApplicationUser>>(existingUsers, responseHeaders, returnCode);
  }

  /**
  * Delete an existing user.
  */
  @DeleteMapping("/{key}")
  public ResponseEntity<String> deleteUser(@PathVariable("key") String key) {
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    String responseBody = "";
    applicationUserRepository.deleteById(key);
    return new ResponseEntity<String>(responseBody, responseHeaders, returnCode);
  }
}
