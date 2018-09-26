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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
/**
 * Zuul filter which dynamically routes requests based on the URL.
 */
public class RoutingFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger(RoutingFilter.class);

  @Autowired
  AeselDiscoveryService discoveryClient;

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
    log.info("Filtering URL: {}", newTail);
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
    boolean routeMatched = false;
    if (urlPathList.length > 1) {

      // Look for the scene root
      if (urlPathList[1].equals("scene")) {

        // Do we have an object/property request?
        if (urlPathList.length > 3) {
          if (urlPathList[3].equals("object") || urlPathList[3].equals("property")) {

            // Object or property request, so route to CLyman
            ServiceInstance targetInstance = discoveryClient.findClyman(urlPathList[2]);
            if (targetInstance != null) {
              log.info("Routing Request to {}", targetInstance.getHost());
              hostname = targetInstance.getHost();
              port = targetInstance.getPort();
              routeMatched = true;
            }
          }
        }

        // Remove the Scene URL elements before routing to CLyman
        if (routeMatched) {
          log.info("Removing Scene from URL");
          newTail = stripSceneUrlElements(urlPathList, parsedUrlList);
        } else {
          // We have a Scene Request, so route to Crazy Ivan
          ServiceInstance targetInstance = discoveryClient.findCrazyIvan();
          if (targetInstance != null) {
            log.info("Routing Request to {}", targetInstance.getHost());
            hostname = targetInstance.getHost();
            port = targetInstance.getPort();
            routeMatched = true;
          }
          if (urlPathList.length == 4) {
            // We need to strip out the scene url elements if we hit
            // the registration API's
            if (urlPathList[3].equals("register") || urlPathList[3].equals("deregister") || urlPathList[3].equals("align")) {
              newTail = "/" + stripSceneUrlElements(urlPathList, parsedUrlList);
            }
          }
        }
      } else if (urlPathList[1].equals("asset")) {
        // We have an asset request, so route to AVC
        ServiceInstance targetInstance = discoveryClient.findAvc();
        if (targetInstance != null) {
          log.info("Routing Request to {}", targetInstance.getHost());
          hostname = targetInstance.getHost();
          port = targetInstance.getPort();
          routeMatched = true;
        }
      }
    }
    if (!routeMatched) {
      log.warn("Unable to parse URL Path: {}", parsedUrlList[0]);
      for (String pathElt : urlPathList) {
        log.warn(pathElt);
      }
    }
    try {
      // Set the URL of the request to a new URL with the existing protocol
      log.info("Returning final URL {}://{}:{}/{}", currentUrl.getProtocol(), hostname, port, newTail);
      context.setRouteHost(new URL(currentUrl.getProtocol(), hostname, port, ""));
      context.put("requestURI", newTail);
    } catch (Exception e) {
      log.error("Error setting service URL", e);
    }
    return null;
  }

}
