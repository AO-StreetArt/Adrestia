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

import com.ao.adrestia.model.SceneLocation;
import com.ao.adrestia.repo.SceneLocationRepository;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
* Rest Controller which provides read access for scenes assigned to a cluster.
* This is important to allow Crazy Ivan instances to keep their cache
* up-to-date, but is not really of use to end-users.
*/
@RestController
@RequestMapping(path = "/cluster")
public class SceneLocationController {

  private static Logger log =
      LoggerFactory.getLogger("adrestia.SceneLocationController");

  @Autowired
  SceneLocationRepository sceneLocations;

  /**
  * Access the assigned scenes for a cluster.
  */
  @RequestMapping(path = "/{cluster}", method = RequestMethod.GET)
  public ResponseEntity<List<String>> getSceneLocation(
      @PathVariable("cluster") String cluster) {
    log.info("Responding to Scene Location request");
    // Set up a success response code
    HttpStatus returnCode = HttpStatus.OK;
    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Find cluster locations
    List<SceneLocation> clusterLocations =
        sceneLocations.findByClusterIdentifier(cluster);
    List<String> clusterScenes = new ArrayList<String>();
    for (SceneLocation loc : clusterLocations) {
      if (!(clusterScenes.contains(loc.sceneIdentifier))) {
        clusterScenes.add(loc.sceneIdentifier);
      }
    }

    // Return the cluster locations
    if (clusterLocations.size() == 0) {
      log.warn("No Scene Locations found");
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
    }
    return new ResponseEntity<List<String>>(clusterScenes, responseHeaders, returnCode);
  }
}
