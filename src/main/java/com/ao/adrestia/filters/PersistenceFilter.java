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
import com.ao.adrestia.model.SceneLocation;
import com.ao.adrestia.repo.SceneLocationRepository;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
/**
 * Zuul filter which runs after a request is routed, and executes updates
 * based on that request when necessary.
 */
public class PersistenceFilter extends ZuulFilter {

  private static Logger log = LoggerFactory.getLogger("adrestia.PersistenceFilter");

  RestTemplate restTemplate = new RestTemplate();

  @Value("${service.discovery.active}")
  private String discoveryActive;

  @Value("${service.static.cluster}")
  private String staticCluster;

  @Autowired
  SceneLocationRepository sceneLocations;

  @Autowired
  AeselDiscoveryService discoveryClient;

  @Override
  public String filterType() {
    return "post";
  }

  @Override
  public int filterOrder() {
    return 4;
  }

  @Override
  public boolean shouldFilter() {
    // Only execute on successful POST requests
    RequestContext context = RequestContext.getCurrentContext();
    HttpServletRequest request = context.getRequest();
    String method = request.getMethod();
    return (method.equals("PUT") && context.getResponseStatusCode() == 200);
  }

  @Override
  public Object run() {
    log.info("Executing Persistence Filter");

    // Find URL path information
    RequestContext context = RequestContext.getCurrentContext();
    HttpServletRequest currentReq = context.getRequest();
    URL currentUrl = context.getRouteHost();
    String urlTail = currentReq.getRequestURI();
    String[] parsedUrlList = urlTail.split(Pattern.quote("?"), 2);
    String[] initialUrlPathList = parsedUrlList[0].split(Pattern.quote("/"), 0);
    String[] urlPathList = null;
    // The first element can be an empty string, which needs to get removed
    if (initialUrlPathList[0].equals("")) {
      urlPathList = Arrays.copyOfRange(initialUrlPathList, 1, initialUrlPathList.length);
    } else {
      urlPathList = initialUrlPathList;
    }

    // Make sure that we have a Scene Creation message
    if (urlPathList.length == 3) {
      if (urlPathList[1].equals("scene")) {

        // Select an Ivan instance to assign the scene to
        ServiceInstance targetInstance = discoveryClient.findCrazyIvan();
        String clusterId = null;
        if (targetInstance != null) {
          Map<String,String> tags = targetInstance.getMetadata();
          if (tags.containsKey("cluster")) {
            clusterId = tags.get("cluster");
          } else {
            log.warn("Unable to find cluster tag on Ivan instance: {}", targetInstance.getHost());
          }
        }

        // If we have a dev cluster, we're going to add the cluster name manually
        if (discoveryActive.equals("false") && clusterId == null) {
          clusterId = staticCluster;
        }

        // Call the Cache API for all Ivan instances in the same
        //   cluster as the selected instance.
        if (clusterId != null) {
          for (ServiceInstance instance : discoveryClient.findCrazyIvanByCluster(clusterId)) {
            String cacheUrl = currentUrl.getProtocol() + "://" + instance.getHost()
                + ":" + instance.getPort() + "/v1/scene/cache/" + urlPathList[2];
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>("", headers);
            ResponseEntity<String> response =
                restTemplate.exchange(cacheUrl, HttpMethod.PUT, entity, String.class);
            //ResponseEntity<String> response = restTemplate.put(cacheUrl, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
              log.error("Error Encountered during Ivan Cache Call: {}",
                  String.valueOf(response.getStatusCode()));
            }
          }
        }

        // Persist a Scene Location Entry
        if (clusterId != null) {
          SceneLocation newLocation = new SceneLocation();
          newLocation.sceneIdentifier = urlPathList[2];
          newLocation.clusterIdentifier = clusterId;
          sceneLocations.save(newLocation);
        }
      }
    }
    return null;
  }

}
