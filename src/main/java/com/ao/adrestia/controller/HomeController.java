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

@SuppressWarnings("unused")
@Controller
public class HomeController {

  @Value(value = "${server.auth.active}")
  private boolean authActive;

  @Autowired
  ApplicationUserRepository userRepository;

  private final Logger logger = LoggerFactory.getLogger("adrestia.AuthController");

  @RequestMapping(value = "/portal/home", method = RequestMethod.GET)
  protected String home(final Map<String, Object> model, final Principal principal) {
    logger.info("Home page");
    if (principal == null && authActive) {
      logger.warn("No Principal Detected");
      return "redirect:/portal/login";
    } else if (authActive) {
      // Add user information to the page
      List<ApplicationUser> users = userRepository.findByUsername(principal.getName());
      if (users.size() > 0) {
        model.put("userId", users.get(0).id);
        model.put("userName", users.get(0).username);
        model.put("isAdmin", String.valueOf(users.get(0).isAdmin));
        String projectsString = "";
        List<String> favProjects = users.get(0).getFavoriteProjects();
        for (int i = 0; i < favProjects.size(); i++) {
          if (i > 1) projectsString = projectsString + ",";
          projectsString = projectsString + favProjects.get(i);
        }
        model.put("projectsString", projectsString);
        String scnString = "";
        List<String> favScenes = users.get(0).getFavoriteScenes();
        for (int i = 0; i < favScenes.size(); i++) {
          if (i > 1) scnString = scnString + ",";
          scnString = scnString + favScenes.get(i);
        }
        model.put("scenesString", scnString);
      } else {
        logger.warn("No User found for principal");
        return "redirect:/portal/login";
      }
    } else {
      model.put("isAdmin", "");
      model.put("userName", "");
      model.put("projects", "");
    }
    return "home";
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  protected String resolveRoot(final Map<String, Object> model, final Principal principal) {
    return "forward:/portal/home";
  }

}
