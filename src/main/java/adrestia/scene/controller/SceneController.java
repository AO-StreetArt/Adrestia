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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import org.zeromq.ZMQ;

/**
* Rest Controller defining the Scene API.
* Responsible for handling and responding to all Scene API Requests.
*/
@RestController
@RequestMapping(path = "/v1/scene")
public class SceneController {

  // How many retries should we attempt prior to reporting a failure
  @Value("${server.ivan.retries}")
  private int requestRetries;
  // How many milliseconds to wait for a reply
  @Value("${server.ivan.timeout}")
  private int requestTimeout;

  // ZMQ Context
  @Autowired
  DvsManager serviceManager;

  // Scene Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.SceneController");

  // Execute a single Crazy Ivan Transaction
  private SceneList ivanTransaction(SceneList inpScene) {
    // Set up a default return Scene List
    Scene[] baseReturnScns = new Scene[0];
    SceneList returnSceneList = new SceneList(inpScene.getMsgType(),
        1, baseReturnScns, 120, "Error Processing Request", "");

    // Send the information to Crazy Ivan
    try {
      // Construct our JSON from the Scene List
      ObjectMapper mapper = new ObjectMapper();
      String ivanMsg = mapper.writeValueAsString(inpScene);
      logger.debug("Crazy Ivan Message: " + ivanMsg);

      // Send the message to Crazy Ivan
      String replyString =
          serviceManager.sendToIvan(ivanMsg, requestTimeout, requestRetries);
      logger.debug("Crazy Ivan Response: " + replyString);

      // Convert the Response back to a Scene List
      if (replyString != null) {
        returnSceneList = mapper.readValue(replyString, SceneList.class);
      }
    } catch (Exception e) {
      logger.error("Error Retrieving Value from Crazy Ivan: ", e);
    }
    return returnSceneList;
  }

  // Get a Scene from Crazy Ivan
  private SceneList retrieveScene(String name) {
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    // Construct a Scene List, which we will then convert to JSON
    Scene scn =
        new Scene("", name, "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    Scene[] scnArray = new Scene[1];
    scnArray[0] = scn;
    SceneList inpSceneList = new SceneList(2, 1, scnArray, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    return ivanTransaction(inpSceneList);
  }

  // Save a scene to Crazy Ivan
  private SceneList saveScene(Scene inpScene, boolean sceneExists) {
    // Construct a Scene List, which we will then convert to JSON
    Scene[] baseInpScns = {inpScene};
    int msgType = 0;
    if (sceneExists) {
      msgType = 1;
    }
    SceneList inpSceneList =
        new SceneList(msgType, 1, baseInpScns, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    return ivanTransaction(inpSceneList);
  }

  // Delete a scene from Crazy Ivan
  private SceneList removeScene(String key) {
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    // Construct a Scene List, which we will then convert to JSON
    Scene scn =
        new Scene(key, "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    Scene[] scnArray = new Scene[1];
    scnArray[0] = scn;
    SceneList inpSceneList = new SceneList(3, 1, scnArray, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    return ivanTransaction(inpSceneList);
  }

  /**
  * Scene Retrieval.
  * Scene name input as path variable, no Request Parameters accepted.
  */
  @RequestMapping(path = "/{name}", method = RequestMethod.GET)
  public ResponseEntity<Scene> getScene(@PathVariable("name") String name) {
    logger.info("Responding to Scene Get Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene returnScn =
        new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus returnCode = HttpStatus.OK;

    SceneList ivanResponse = retrieveScene(name);

    // If we have a successful response, then we pull the first value
    if (ivanResponse.getNumRecords() > 0
        && ivanResponse.getErrorCode() == 100) {
      returnScn = ivanResponse.getSceneList()[0];
    } else {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("Failure Registered.  Ivan Response Error Code and Length:");
      logger.debug(ivanResponse.getNumRecords());
      logger.debug(ivanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(returnScn, responseHeaders, returnCode);
  }

  /**
  * Scene Create/Update.
  * Scene name input as path variable, no Request Parameters accepted.
  * POST Data read in with scene data.
  */
  @RequestMapping(path = "/{name}",
      headers = "Content-Type=application/json",
      method = RequestMethod.POST)
  public ResponseEntity<Scene> updateScene(
      @PathVariable("name") String name,
      @RequestBody Scene inpScene) {
    logger.info("Responding to Scene Save Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene returnScn =
        new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus returnCode = HttpStatus.OK;

    // See if we can find the scene requested
    SceneList ivanResponse = retrieveScene(name);

    // If we have a successful response, then the scene exists
    boolean sceneExists = false;
    if (ivanResponse.getNumRecords() > 0
        && ivanResponse.getErrorCode() == 100) {
      sceneExists = true;
      logger.debug("Existing Scene found in Crazy Ivan");
      // Set the key on the input scene to the key from the response
      String ivanRespKey = ivanResponse.getSceneList()[0].getKey();
      if (ivanRespKey != null && !ivanRespKey.isEmpty()) {
        inpScene.setKey(ivanRespKey);
        logger.debug("Ivan Response Key: " + ivanRespKey);
      }
    }

    // Update the scene
    inpScene.setName(name);
    SceneList updateResponse = saveScene(inpScene, sceneExists);

    // If we have a successful response, then we pull the first value
    if (updateResponse.getNumRecords() > 0
        && updateResponse.getErrorCode() == 100) {
      returnScn = updateResponse.getSceneList()[0];
    } else {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("Failure Registered.  Ivan Response Error Code and Length:");
      logger.debug(ivanResponse.getNumRecords());
      logger.debug(ivanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(returnScn, responseHeaders, returnCode);
  }

  /**
  * Scene Delete.
  * Scene name input as path variable, no Request Parameters accepted.
  */
  @RequestMapping(path = "/{name}", method = RequestMethod.DELETE)
  public ResponseEntity<Scene> deleteScene(@PathVariable("name") String name) {
    logger.info("Responding to Scene Delete Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene returnScn =
        new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus returnCode = HttpStatus.OK;

    // See if we can find the scene requested
    SceneList ivanResponse = retrieveScene(name);

    // If we have a successful response, then the scene exists
    boolean sceneExists = false;
    String ivanRespKey = null;
    if (ivanResponse.getNumRecords() > 0
        && ivanResponse.getErrorCode() == 100) {
      sceneExists = true;
      // Set the key on the input scene to the key from the response
      ivanRespKey = ivanResponse.getSceneList()[0].getKey();
      if (ivanRespKey != null && !ivanRespKey.isEmpty()) {
        sceneExists = true;
        logger.debug("Existing Scene found in Crazy Ivan");
      }
    }

    // Delete the scene
    SceneList deleteResponse = null;
    if (sceneExists) {
      deleteResponse = removeScene(ivanRespKey);
    }

    // If we have a successful response, then we pull the first value
    if (deleteResponse != null) {
      if (deleteResponse.getNumRecords() > 0
          && deleteResponse.getErrorCode() == 100) {
        returnScn = deleteResponse.getSceneList()[0];
      } else {
        returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
        logger.debug("Failure Registered.  Ivan Response Err Code and Length:");
        logger.debug(ivanResponse.getNumRecords());
        logger.debug(ivanResponse.getErrorCode());
      }
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(returnScn, responseHeaders, returnCode);
  }

  /**
  * Scene Query.
  * No Request Parameters accepted.
  * POST Data read in with scene data.
  */
  @RequestMapping(path = "/data",
      headers = "Content-Type=application/json",
      method = RequestMethod.POST)
  public ResponseEntity<SceneList> queryScene(@RequestBody Scene inpScene) {
    logger.info("Responding to Scene Query Request");
    String[] assets = new String[0];
    String[] tags = new String[0];
    UserDevice[] devices = new UserDevice[0];
    Scene returnScn =
        new Scene("", "", "", -9999.0, -9999.0, 0.0, assets, tags, devices);
    HttpStatus returnCode = HttpStatus.OK;

    // Construct a Scene List, which we will then convert to JSON
    Scene[] scnArray = new Scene[1];
    scnArray[0] = inpScene;
    SceneList inpSceneList = new SceneList(2, 1, scnArray, 100, "", "");
    // Send the Scene List to Crazy Ivan and get the response
    SceneList ivanResponse = ivanTransaction(inpSceneList);

    // If we have a failure response, then return a failure error code
    if (ivanResponse.getNumRecords() == 0
        || ivanResponse.getErrorCode() > 100) {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("Failure Registered.  Ivan Response Error Code and Length:");
      logger.debug(ivanResponse.getNumRecords());
      logger.debug(ivanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<SceneList>(
        ivanResponse, responseHeaders, returnCode);
  }
}
