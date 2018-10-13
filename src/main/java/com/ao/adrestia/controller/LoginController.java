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

import com.auth0.AuthenticationController;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SuppressWarnings("unused")
@Controller
public class LoginController {

  @Autowired
  private AuthenticationController controller;
  @Value(value = "${com.auth0.domain}")
  private String domain;
  private final Logger logger = LoggerFactory.getLogger("adrestia.AuthController");

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  protected String login(final HttpServletRequest req) {
    logger.debug("Performing login");
    String redirectUri = req.getScheme() + "://" + req.getServerName() + ":"
        + req.getServerPort() + "/callback";
    String authorizeUrl = controller.buildAuthorizeUrl(req, redirectUri)
        .withAudience(String.format("https://%s/userinfo", domain))
        .build();
    return "redirect:" + authorizeUrl;
  }

}
