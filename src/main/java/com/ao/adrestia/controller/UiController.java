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
  protected String sceneBrowser(final Map<String, Object> model, final Principal principal) {
    logger.info("Request for Scene Browser");
    model.put("test", "test");
    return "sceneBrowser";
  }

}
