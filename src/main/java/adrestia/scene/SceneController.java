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

package adrestia;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Semaphore;

import javax.annotation.PreDestroy;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
* Rest Controller defining the Scene API
* Responsible for handling and responding to all Scene API Requests
*/
@RestController
@RequestMapping(path = "/v1/scene")
public class SceneController {

  // How many retries should we attempt prior to reporting a failure
  private final static int REQUEST_RETRIES = 3;
  // How many milliseconds to wait for a reply
  private final static int REQUEST_TIMEOUT = 5000;

  // ZMQ Context
  @Autowired
  DvsManager serviceManager;

  // Scene Controller Logger
  private static final Logger logger = LogManager.getLogger("adrestia.SceneController");

  // Get a Scene from Crazy Ivan
  private SceneList retrieve_scene(String name) {
    // Set up default values
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene base_return_scn = new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    Scene[] base_return_scns = {base_return_scn};
    SceneList return_scn = new SceneList(2, 1, base_return_scns, 120, "Error in Processing Request", "");

    // Retrieve the needed information from Crazy Ivan
    try {
      // Construct a Scene List, which we will then convert to JSON
      Scene scn = new Scene("", name, "", -9999.0, -9999.0, 0.0, assets, tags, devices);
      Scene[] scn_array = new Scene[1];
      scn_array[0] = scn;
      SceneList list = new SceneList(2, 1, scn_array, 100, "", "");

      // Construct our JSON from the Scene List
      ObjectMapper mapper = new ObjectMapper();
      String ivan_msg = mapper.writeValueAsString(list);
      logger.debug(ivan_msg);

      // Send the message to Crazy Ivan
      String reply_string = serviceManager.send_to_ivan(ivan_msg, REQUEST_TIMEOUT, REQUEST_RETRIES);

      // Convert the Response back to a Scene List
      if (reply_string != null) {
        return_scn = mapper.readValue(reply_string, SceneList.class);
      }

    } catch(Exception e) {
      logger.error("Error Retrieving Value from Crazy Ivan");
      logger.error(e.getMessage());
    }
    return return_scn;
  }

  // Save a scene to Crazy Ivan
  private SceneList save_scene(Scene inpScene, boolean sceneExists) {
    Scene[] base_inp_scns = {inpScene};
    int msg_type = 0;
    if (sceneExists) {
      msg_type = 1;
    }
    SceneList inp_scene_list = new SceneList(msg_type, 1, base_inp_scns, 100, "", "");
    Scene[] base_return_scns = new Scene[0];
    SceneList return_scene_list = new SceneList(msg_type, 1, base_return_scns, 120, "Error Processing Request", "");

    // Save the information to Crazy Ivan
    try {
      // Construct our JSON from the Scene List
      ObjectMapper mapper = new ObjectMapper();
      String ivan_msg = mapper.writeValueAsString(inp_scene_list);
      logger.debug(ivan_msg);

      // Send the message to Crazy Ivan
      String reply_string = serviceManager.send_to_ivan(ivan_msg, REQUEST_TIMEOUT, REQUEST_RETRIES);

      // Convert the Response back to a Scene List
      if (reply_string != null) {
        return_scene_list = mapper.readValue(reply_string, SceneList.class);
      }
    } catch(Exception e) {
      logger.error("Error Retrieving Value from Crazy Ivan");
      logger.error(e.getMessage());
    }
    return return_scene_list;
  }

  /**
  * Scene Retrieval
  * Scene name input as path variable, no Request Parameters accepted
  */
  @RequestMapping(path = "/{name}", method = RequestMethod.GET)
  public ResponseEntity<Scene> get_scene(@PathVariable("name") String name) {
    logger.debug("Responding to Scene Get Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene return_scn = new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus return_code = HttpStatus.OK;

    SceneList ivan_response = retrieve_scene(name);

    // If we have a successful response, then we pull the first value
    if (ivan_response.getNumRecords() > 0 && ivan_response.getErrorCode() == 100) {
      return_scn = ivan_response.getSceneList()[0];
    } else {
      return_code = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(return_scn, responseHeaders, return_code);
  }

  /**
  * Scene Create/Update
  * Scene name input as path variable, no Request Parameters accepted
  * POST Data read in with scene data
  */
  @RequestMapping(path = "/{name}", headers="Content-Type=application/json", method = RequestMethod.POST)
  public ResponseEntity<Scene> update_scene(@PathVariable("name") String name, @RequestBody Scene inp_scene) {
    logger.debug("Responding to Scene Get Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene return_scn = new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus return_code = HttpStatus.OK;

    // See if we can find the scene requested
    SceneList ivan_response = retrieve_scene(name);

    // If we have a successful response, then the scene exists
    boolean scene_exists = false;
    if (ivan_response.getNumRecords() > 0 && ivan_response.getErrorCode() == 100) {
      scene_exists = true;
    }

    // Update the scene
    inp_scene.setName(name);
    SceneList update_response = save_scene(inp_scene, scene_exists);

    // If we have a successful response, then we pull the first value
    if (update_response.getNumRecords() > 0 && update_response.getErrorCode() == 100) {
      return_scn = update_response.getSceneList()[0];
    } else {
      return_code = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(return_scn, responseHeaders, return_code);
  }

  /**
  * Scene Delete
  * Scene name input as path variable, no Request Parameters accepted
  */
  @RequestMapping(path = "/{name}", method = RequestMethod.DELETE)
  public ResponseEntity<Scene> delete_scene(@PathVariable("name") String name) {
    // Build a new scene
    String[] assets;
    assets = new String[2];
    assets[0] = "1";
    String[] tags;
    tags = new String[2];
    tags[0] = "1";
    UserDevice[] devices = new UserDevice[0];
    Scene scn = new Scene("A", name, "C", 1.0, 2.0, 3.0, assets, tags, devices);

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(scn, responseHeaders, HttpStatus.OK);
  }

  /**
  * Scene Query
  * No Request Parameters accepted
  * POST Data read in with scene data
  */
  @RequestMapping(path = "/data", headers="Content-Type=application/json", method = RequestMethod.POST)
  public ResponseEntity<SceneList> query_scene(@RequestBody Scene inp_scene) {
    String region = "Bad";
    if (inp_scene != null) {
      region = inp_scene.getRegion();
    }
    // Build a new scene
    String[] assets;
    assets = new String[2];
    assets[0] = "1";
    assets[1] = "2";
    String[] tags;
    tags = new String[2];
    tags[0] = "1";
    tags[1] = "2";
    UserDevice[] devices = new UserDevice[0];
    Scene scn = new Scene("A", "B", region, 1.0, 2.0, 3.0, assets, tags, devices);
    Scene[] scn_array = new Scene[1];
    scn_array[0] = scn;

    // Build a Scene List
    SceneList list = new SceneList(0, 1, scn_array, 100, "", "");

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    return new ResponseEntity<SceneList>(list, responseHeaders, HttpStatus.OK);
  }
}
