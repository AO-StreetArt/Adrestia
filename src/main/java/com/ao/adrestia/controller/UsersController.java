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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
  private String mongoCollectionName = "applicationUser";

  @Autowired
  ApplicationUserRepository applicationUserRepository;

  @Autowired
  MongoDatabase mongoDb;
  MongoCollection<Document> mongoCollection = null;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
  * Use the Mongo Client to access the database and collection.
  */
  @PostConstruct
  public void init() {
    mongoCollection = mongoDb.getCollection(mongoCollectionName);
  }

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

  private BasicDBObject genUpdateQuery(String attrKey, String attrVal, String opType) {
    BasicDBObject update = new BasicDBObject();
    update.put(attrKey, attrVal);
    return new BasicDBObject(opType, update);
  }

  private BasicDBObject genIdQuery(String id) {
    BasicDBObject query = new BasicDBObject();
    query.put("_id", new ObjectId(id));
    return query;
  }

  /**
  * Update an existing user.
  */
  @PutMapping("/{key}")
  public ResponseEntity<ApplicationUser> updateUser(
      @RequestBody ApplicationUser user,
      @PathVariable("key") String key) {
    log.info("Updating Existing User");
    BasicDBObject updateQuery = new BasicDBObject();
    if (user.getUsername() != null && !(user.getUsername().isEmpty())) {
      updateQuery.put("username", user.getUsername());
    }
    if (user.getUsername() != null && !(user.getUsername().isEmpty())) {
      updateQuery.put("password", bCryptPasswordEncoder.encode(user.getPassword()););
    }
    if (user.getEmail() != null && !(user.getEmail().isEmpty())) {
      updateQuery.put("email", user.getEmail());
    }
    if (user.getIsAdmin() != null) {
      updateQuery.put("isAdmin", user.getIsAdmin());
    }
    if (user.getIsActive() != null) {
      updateQuery.put("isActive", user.getIsActive());
    }

    UpdateResult result = mongoCollection.updateOne(genIdQuery(key),
        new BasicDBObject("$set", updateQuery), new UpdateOptions());

    // Set the http response code
    HttpStatus returnCode = HttpStatus.OK;
    if (result.getModifiedCount() < 1) {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("No documents modified for user update");
    }
    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<String>("", responseHeaders, returnCode);
  }

  private ResponseEntity<String> updateArrayAttr(String userKey,
      String attrKey, String attrVal, String updType) {
    BasicDBObject updateQuery = genUpdateQuery(attrKey, attrVal, updType);
    BasicDBObject query = genIdQuery(userKey);
    UpdateResult result = mongoCollection.updateOne(query, updateQuery, new UpdateOptions());
    // Set the http response code
    HttpStatus returnCode = HttpStatus.OK;
    if (result.getModifiedCount() < 1) {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      log.debug("No documents modified for array attribute update");
    }
    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    return new ResponseEntity<String>("", responseHeaders, returnCode);
  }

  /**
  * Add a favorite project to an existing user.
  */
  @PutMapping("/{key}/projects/{projectKey}")
  public ResponseEntity<String> addUserFavProject(
      @PathVariable("key") String key,
      @PathVariable("projectKey") String projectKey) {
    log.info("Adding Favorite Project to user");
    return updateArrayAttr(key, "favoriteProjects", projectKey, "$push");
  }

  /**
  * Remove a favorite project from an existing user.
  */
  @DeleteMapping("/{key}/projects/{projectKey}")
  public ResponseEntity<String> removeUserFavProject(
      @PathVariable("key") String key,
      @PathVariable("projectKey") String projectKey) {
    log.info("Removing Favorite Project from user");
    return updateArrayAttr(key, "favoriteProjects", projectKey, "$pull");
  }

  /**
  * Add a favorite scene to an existing user.
  */
  @PutMapping("/{key}/scenes/{sceneKey}")
  public ResponseEntity<String> addUserFavScene(
      @PathVariable("key") String key,
      @PathVariable("sceneKey") String sceneKey) {
    log.info("Adding Favorite Scene to user");
    return updateArrayAttr(key, "favoriteScenes", sceneKey, "$push");
  }

  /**
  * Remove a favorite scene from an existing user.
  */
  @DeleteMapping("/{key}/scenes/{sceneKey}")
  public ResponseEntity<String> removeUserFavScene(
      @PathVariable("key") String key,
      @PathVariable("sceneKey") String sceneKey) {
    log.info("Remove Favorite Scene from user");
    return updateArrayAttr(key, "favoriteScenes", sceneKey, "$pull");
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
    for (ApplicationUser user : existingUsers) {
      user.password = "";
    }
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
