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

package com.ao.adrestia.filters;

import com.ao.adrestia.discovery.AeselDiscoveryService;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
/**
 * Zuul filter which dynamically routes requests based on the URL.
 */
public class RoutingFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger("adrestia.RoutingFilter");

  @Autowired
  AeselDiscoveryService discoveryClient;

  // Ivan (Scene Service) username
  @Value(value = "${service.ivan.username:}")
  private String ivanUsername;

  // Ivan (Scene Service) password
  @Value(value = "${service.ivan.password:}")
  private String ivanPassword;

  // CLyman (Object Service) username
  @Value(value = "${service.clyman.username:}")
  private String clymanUsername;

  // CLyman (Object Service) password
  @Value(value = "${service.clyman.password:}")
  private String clymanPassword;

  // Asset Version Control username
  @Value(value = "${service.avc.username:}")
  private String avcUsername;

  // Asset Version Control password
  @Value(value = "${service.avc.password:}")
  private String avcPassword;

  // Projects Service username
  @Value(value = "${service.projects.username:}")
  private String projectsUsername;

  // Projects Service password
  @Value(value = "${service.projects.password:}")
  private String projectsPassword;

  @Value("${server.auth.active:false}")
  private boolean httpAuthActive;

  @Override
  public String filterType() {
    return "route";
  }

  @Override
  public int filterOrder() {
    return 2;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  // Rebuild a URL without Scene elements
  private String stripSceneUrlElements(String[] tail, String[] parsedUrlList) {
    String newTail = tail[0];
    for (int i = 3; i < tail.length; i++) {
      newTail = newTail + "/" + tail[i];
    }
    if (parsedUrlList.length > 1) {
      newTail = newTail + "?" + parsedUrlList[1];
    }
    return newTail;
  }

  @Override
  public Object run() {
    RequestContext context = RequestContext.getCurrentContext();
    URL currentUrl = context.getRouteHost();
    HttpServletRequest currentReq = context.getRequest();

    // Find URL path information
    String urlTail = currentReq.getRequestURI();
    String newTail = urlTail;
    log.debug("Filtering URL: {}", newTail);
    String[] parsedUrlList = urlTail.split(Pattern.quote("?"), 2);
    String[] initialUrlPathList = parsedUrlList[0].split(Pattern.quote("/"), 0);
    String[] urlPathList = null;
    // The first element can be an empty string, which needs to get removed
    if (initialUrlPathList[0].equals("")) {
      urlPathList = Arrays.copyOfRange(initialUrlPathList, 1, initialUrlPathList.length);
    } else {
      urlPathList = initialUrlPathList;
    }
    String hostname = "localhost";
    int port = 9000;
    boolean isIvanRequest = false;
    boolean isClymanRequest = false;
    boolean isAvcRequest = false;
    boolean isProjectRequest = false;
    if (urlPathList.length > 1) {

      // Look for the scene root
      if (urlPathList[1].equals("scene")) {

        // Do we have an object/property request?
        if (urlPathList.length > 3) {
          if (urlPathList[3].equals("object") || urlPathList[3].equals("property")) {

            // Object or property request, so route to CLyman
            ServiceInstance targetInstance = discoveryClient.findClyman(urlPathList[2]);
            if (targetInstance != null) {
              log.debug("Routing Request to {}", targetInstance.getHost());
              hostname = targetInstance.getHost();
              port = targetInstance.getPort();
              isClymanRequest = true;
            }
          }
        }

        // Remove the Scene URL elements before routing to CLyman
        if (isClymanRequest) {
          log.info("Removing Scene from URL");
          newTail = stripSceneUrlElements(urlPathList, parsedUrlList);
        } else {
          // We have a Scene Request, so route to Crazy Ivan
          ServiceInstance targetInstance = discoveryClient.findCrazyIvan();
          if (targetInstance != null) {
            log.debug("Routing Request to {}", targetInstance.getHost());
            hostname = targetInstance.getHost();
            port = targetInstance.getPort();
            isIvanRequest = true;
          }
          if (urlPathList.length == 4) {
            // We need to strip out the scene url elements if we hit
            // the registration API's
            if (urlPathList[3].equals("register") || urlPathList[3].equals("deregister") || urlPathList[3].equals("align")) {
              newTail = "/" + stripSceneUrlElements(urlPathList, parsedUrlList);
            }
          }
        }

      } else if (urlPathList[1].equals("asset") || urlPathList[1].equals("history") || urlPathList[1].equals("relationship") || urlPathList[1].equals("collection")) {
        // We have an asset request, so route to AVC
        ServiceInstance targetInstance = discoveryClient.findAvc();
        if (targetInstance != null) {
          log.debug("Routing Request to {}", targetInstance.getHost());
          hostname = targetInstance.getHost();
          port = targetInstance.getPort();
          isAvcRequest = true;
        }
      } else if (urlPathList[1].equals("project")) {
        // We have a project request, so route to the Projects service
        ServiceInstance targetInstance = discoveryClient.findProjectService();
        if (targetInstance != null) {
          log.debug("Routing Request to {}", targetInstance.getHost());
          hostname = targetInstance.getHost();
          port = targetInstance.getPort();
          isProjectRequest = true;
        }
      }
    }

    if (!(isAvcRequest || isIvanRequest || isClymanRequest || isProjectRequest)) {
      log.warn("Unable to parse URL Path: {}", parsedUrlList[0]);
      for (String pathElt : urlPathList) {
        log.warn(pathElt);
      }
    }

    // Determine the protocol of the request
    String originalUrl = context.getRequest().getRequestURL().toString();
    String[] originalUrlSplitList = originalUrl.split(":", 2);
    String routedProtocol = "";
    if (originalUrlSplitList.length > 0) {
      routedProtocol = originalUrlSplitList[0];
    }

    try {
      // Set the URL of the request to a new URL with the existing protocol
      log.info("Returning final URL {}://{}:{}/{}", routedProtocol, hostname, port, newTail);
      context.setRouteHost(new URL(routedProtocol, hostname, port, ""));
      context.put("requestURI", newTail);
    } catch (Exception e) {
      log.error("Error setting service URL", e);
    }

    // If Authentication is enabled, then inject Basic Auth credentials
    if (httpAuthActive) {
      String credentialsString = "";
      if (isAvcRequest) {
        log.debug("Setting AVC Credentials with User {}", avcUsername);
        credentialsString =
            Base64.getEncoder().encodeToString((avcUsername + ":" + avcPassword).getBytes(StandardCharsets.ISO_8859_1));
      } else if (isClymanRequest) {
        log.debug("Setting CLyman Credentials with User {}", clymanUsername);
        credentialsString =
            Base64.getEncoder().encodeToString((clymanUsername + ":" + clymanPassword).getBytes(StandardCharsets.ISO_8859_1));
      } else if (isIvanRequest) {
        log.debug("Setting Ivan Credentials with User {}", ivanUsername);
        credentialsString =
            Base64.getEncoder().encodeToString((ivanUsername + ":" + ivanPassword).getBytes(StandardCharsets.ISO_8859_1));
      } else if (isProjectRequest) {
        log.debug("Setting Projects Credentials with User {}", projectsUsername);
        credentialsString =
            Base64.getEncoder().encodeToString((projectsUsername + ":" + projectsPassword).getBytes(StandardCharsets.ISO_8859_1));
      }
      context.addZuulRequestHeader("Authorization", "Basic " + credentialsString);
    }
    return null;
  }

}
