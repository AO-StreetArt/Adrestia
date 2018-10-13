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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SuppressWarnings("unused")
@Controller
public class AdrestiaErrorController implements ErrorController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String PATH = "/error";

  @RequestMapping("/error")
  protected String error(final RedirectAttributes redirectAttributes) throws IOException {
    logger.error("Handling error");
    redirectAttributes.addFlashAttribute("error", true);
    return "redirect:/login";
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }

}
