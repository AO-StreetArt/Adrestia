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

  // Execute a single Crazy Ivan Transaction
  private SceneList ivan_transaction(SceneList inp_scene) {
    // Set up a default return Scene List
    Scene[] base_return_scns = new Scene[0];
    SceneList return_scene_list = new SceneList(inp_scene.getMsgType(), 1, base_return_scns, 120, "Error Processing Request", "");

    // Send the information to Crazy Ivan
    try {
      // Construct our JSON from the Scene List
      ObjectMapper mapper = new ObjectMapper();
      String ivan_msg = mapper.writeValueAsString(inp_scene);
      logger.debug(ivan_msg);

      // Send the message to Crazy Ivan
      String reply_string = serviceManager.send_to_ivan(ivan_msg, REQUEST_TIMEOUT, REQUEST_RETRIES);

      // Convert the Response back to a Scene List
      if (reply_string != null) {
        return_scene_list = mapper.readValue(reply_string, SceneList.class);
      }
    } catch(Exception e) {
      logger.error("Error Retrieving Value from Crazy Ivan: ", e);
    }
    return return_scene_list;
  }

  // Get a Scene from Crazy Ivan
  private SceneList retrieve_scene(String name) {
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    // Construct a Scene List, which we will then convert to JSON
    Scene scn = new Scene("", name, "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    Scene[] scn_array = new Scene[1];
    scn_array[0] = scn;
    SceneList inp_scene_list = new SceneList(2, 1, scn_array, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    return ivan_transaction(inp_scene_list);
  }

  // Save a scene to Crazy Ivan
  private SceneList save_scene(Scene inpScene, boolean sceneExists) {
    // Construct a Scene List, which we will then convert to JSON
    Scene[] base_inp_scns = {inpScene};
    int msg_type = 0;
    if (sceneExists) {
      msg_type = 1;
    }
    SceneList inp_scene_list = new SceneList(msg_type, 1, base_inp_scns, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    return ivan_transaction(inp_scene_list);
  }

  // Delete a scene from Crazy Ivan
  private SceneList remove_scene(String key) {
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    // Construct a Scene List, which we will then convert to JSON
    Scene scn = new Scene(key, "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    Scene[] scn_array = new Scene[1];
    scn_array[0] = scn;
    SceneList inp_scene_list = new SceneList(3, 1, scn_array, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    return ivan_transaction(inp_scene_list);
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
    logger.debug("Responding to Scene Save Request");
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
      // Set the key on the input scene to the key from the response
      String ivan_resp_key = ivan_response.getSceneList()[0].getKey();
      if (ivan_resp_key != null && !ivan_resp_key.isEmpty()) {
        inp_scene.setKey(ivan_resp_key);
      }
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
    logger.debug("Responding to Scene Delete Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene return_scn = new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus return_code = HttpStatus.OK;

    // See if we can find the scene requested
    SceneList ivan_response = retrieve_scene(name);

    // If we have a successful response, then the scene exists
    boolean scene_exists = false;
    String ivan_resp_key = null;
    if (ivan_response.getNumRecords() > 0 && ivan_response.getErrorCode() == 100) {
      scene_exists = true;
      // Set the key on the input scene to the key from the response
      ivan_resp_key = ivan_response.getSceneList()[0].getKey();
      if (ivan_resp_key != null && !ivan_resp_key.isEmpty()) {
        scene_exists = true;
      }
    }

    // Delete the scene
    SceneList delete_response = null;
    if (scene_exists) {
      delete_response = remove_scene(ivan_resp_key);
    }

    // If we have a successful response, then we pull the first value
    if (delete_response != null) {
      if (delete_response.getNumRecords() > 0 && delete_response.getErrorCode() == 100) {
        return_scn = delete_response.getSceneList()[0];
      } else {
        return_code = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      }
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(return_scn, responseHeaders, return_code);
  }

  /**
  * Scene Query
  * No Request Parameters accepted
  * POST Data read in with scene data
  */
  @RequestMapping(path = "/data", headers="Content-Type=application/json", method = RequestMethod.POST)
  public ResponseEntity<SceneList> query_scene(@RequestBody Scene inp_scene) {
    logger.debug("Responding to Scene Get Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene return_scn = new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus return_code = HttpStatus.OK;

    // Construct a Scene List, which we will then convert to JSON
    Scene[] scn_array = new Scene[1];
    scn_array[0] = inp_scene;
    SceneList inp_scene_list = new SceneList(2, 1, scn_array, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    SceneList ivan_response = ivan_transaction(inp_scene_list);

    // If we have a failure response, then return a failure error code
    if (ivan_response.getNumRecords() == 0 || ivan_response.getErrorCode() > 100) {
      return_code = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<SceneList>(ivan_response, responseHeaders, return_code);
  }
}
