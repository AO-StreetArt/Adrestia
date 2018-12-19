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

import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  @RequestMapping(value = "/assetBrowser", method = RequestMethod.GET)
  protected String assetBrowser(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for Asset Browser");
    model.put("test", "test");
    return "assetBrowser";
  }

  @RequestMapping(value = "/sceneBrowser", method = RequestMethod.GET)
  protected String sceneBrowser(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "keys", defaultValue = "") String sceneKeys) {
    logger.info("Request for Scene Browser");
    model.put("sceneKeys", sceneKeys);
    return "sceneBrowser";
  }

  @RequestMapping(value = "/projectBrowser", method = RequestMethod.GET)
  protected String projectBrowser(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for Project Browser");
    model.put("test", "test");
    return "projectBrowser";
  }

  @RequestMapping(value = "/editProject", method = RequestMethod.GET)
  protected String createProjectUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String projKey) {
    logger.info("Request for Project Edit UI");
    model.put("projKey", projKey);
    return "projectEdit";
  }

  @RequestMapping(value = "/editScene", method = RequestMethod.GET)
  protected String createSceneUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String sceneKey) {
    logger.info("Request for Scene Edit UI");
    model.put("sceneKey", sceneKey);
    return "sceneEdit";
  }

  @RequestMapping(value = "/editAsset", method = RequestMethod.GET)
  protected String createAssetUi(
      final Map<String, Object> model,
      final Principal principal,
      @RequestParam(value = "key", defaultValue = "") String assetKey) {
    logger.info("Request for Asset Edit UI");
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
