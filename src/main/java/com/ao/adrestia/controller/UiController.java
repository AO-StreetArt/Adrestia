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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
* Controller for serving the Web Interface.
*/
@SuppressWarnings("unused")
@Controller
public class UiController {

  private final Logger logger = LoggerFactory.getLogger("adrestia.AuthController");

  @Value(value = "${server.auth.active}")
  private boolean authActive;

  @Autowired
  ApplicationUserRepository userRepository;

  // Pages which display the full navbar need to know whether
  // the logged in user is an admin to know what to display.
  private void addUserDetailsToModel(final Map<String, Object> model, final Principal principal) {
    if (authActive) {
      List<ApplicationUser> users = userRepository.findByUsername(principal.getName());
      if (users.size() > 0) {
        model.put("userName", users.get(0).username);
        model.put("isAdmin", String.valueOf(users.get(0).isAdmin));
      }
    }
  }

  @RequestMapping(value = "/assetBrowser", method = RequestMethod.GET)
  protected String assetBrowser(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for Asset Browser");
    addUserDetailsToModel(model, principal);
    return "assetBrowser";
  }

  @RequestMapping(value = "/sceneBrowser", method = RequestMethod.GET)
  protected String sceneBrowser(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "keys", defaultValue = "") String sceneKeys) {
    logger.info("Request for Scene Browser");
    addUserDetailsToModel(model, principal);
    model.put("sceneKeys", sceneKeys);
    return "sceneBrowser";
  }

  @RequestMapping(value = "/propertyBrowser", method = RequestMethod.GET)
  protected String propertyBrowser(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "scene", defaultValue = "") String sceneKey,
      @RequestParam(value = "object", defaultValue = "") String objKey) {
    logger.info("Request for Property Browser");
    addUserDetailsToModel(model, principal);
    model.put("sceneKey", sceneKey);
    model.put("objKey", objKey);
    return "propertyBrowser";
  }

  @RequestMapping(value = "/projectBrowser", method = RequestMethod.GET)
  protected String projectBrowser(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for Project Browser");
    addUserDetailsToModel(model, principal);
    return "projectBrowser";
  }

  @RequestMapping(value = "/userBrowser", method = RequestMethod.GET)
  protected String userBrowser(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for User Browser");
    return "userBrowser";
  }

  @RequestMapping(value = "/editUser", method = RequestMethod.GET)
  protected String createUserUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String userKey) {
    logger.info("Request for User Edit UI");
    addUserDetailsToModel(model, principal);
    model.put("userKey", userKey);
    return "userEdit";
  }

  @RequestMapping(value = "/editProperty", method = RequestMethod.GET)
  protected String createPropertyUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "scene", defaultValue = "") String sceneKey,
      @RequestParam(value = "property", defaultValue = "") String propKey) {
    logger.info("Request for Property Edit UI");
    addUserDetailsToModel(model, principal);
    model.put("sceneKey", sceneKey);
    model.put("propKey", propKey);
    return "propertyEdit";
  }

  @RequestMapping(value = "/editProject", method = RequestMethod.GET)
  protected String createProjectUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String projKey) {
    logger.info("Request for Project Edit UI");
    addUserDetailsToModel(model, principal);
    model.put("projKey", projKey);
    return "projectEdit";
  }

  @RequestMapping(value = "/editScene", method = RequestMethod.GET)
  protected String createSceneUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String sceneKey) {
    logger.info("Request for Scene Edit UI");
    addUserDetailsToModel(model, principal);
    model.put("sceneKey", sceneKey);
    return "sceneEdit";
  }

  @RequestMapping(value = "/editAsset", method = RequestMethod.GET)
  protected String createAssetUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String assetKey) {
    logger.info("Request for Asset Edit UI");
    addUserDetailsToModel(model, principal);
    model.put("assetKey", assetKey);
    return "assetEdit";
  }

  @RequestMapping(value = "/editObj", method = RequestMethod.GET)
  protected String createObjUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "sceneKey", defaultValue = "") String sceneKey,
      @RequestParam(value = "objKey", defaultValue = "") String objKey) {
    logger.info("Request for Scene Edit UI");
    addUserDetailsToModel(model, principal);
    model.put("sceneKey", sceneKey);
    model.put("objKey", objKey);
    return "objEdit";
  }

  @RequestMapping(value = "/portal/login", method = RequestMethod.GET)
  protected String loginPage(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for Login Page");
    return "login";
  }

}
