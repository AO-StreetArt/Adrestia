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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
* Dao Implementation for Scene Objects using Crazy Ivan.
*/
@Component
public class CrazyIvanConnector extends ZmqConnector implements SceneDao {

  // How many retries should we attempt prior to reporting a failure
  @Value("${server.ivan.retries}")
  private int requestRetries;
  // How many milliseconds to wait for a reply
  @Value("${server.ivan.timeout}")
  private int requestTimeout;

  // Crazy Ivan Connection Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.IvanConnector");

  /**
  * Default empty CrazyIvanConnector constructor.
  */
  public CrazyIvanConnector() {
    super();
  }

  // Send a message to Crazy Ivan, return the response.
  private SceneList transaction(SceneList inpScene) {
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
          send(ivanMsg, requestTimeout, requestRetries, "Ivan");
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

  // Convenience method to turn a Scene into a Scene List
  private SceneList buildSceneList(Scene inpScene, int msgType) {
    Scene[] baseInpScns = {inpScene};
    return new SceneList(msgType, baseInpScns);
  }

  // Execute a CRUD Transaction with Crazy Ivan
  private SceneList crudTransaction(Scene inpScene, int msgType) {
    // Construct a Scene List, which we will then convert to JSON
    SceneList inpSceneList = buildSceneList(inpScene, msgType);
    // Send the Scene List to Crazy Ivan and get the response
    return transaction(inpSceneList);
  }

  // Execute a Registration Transaction with Crazy Ivan
  private SceneList registrationTransaction(String sceneName,
      String deviceId, Transform inpTransform, int registerMsgType) {
    logger.debug("Scene Registration Name: " + sceneName);
    logger.debug("Scene Registration Device " + deviceId);

    // Construct a User Device array
    UserDevice ud = new UserDevice();
    ud.setKey(deviceId);
    // Pass the transform to the user device, if one is passed in
    if (inpTransform != null) {
      logger.debug("Input Transform detected");
      ud.setTransform(inpTransform);
    }
    UserDevice[] devices = {ud};
    // Construct a scene array
    Scene scn = new Scene();
    scn.setName(sceneName);
    scn.setDevices(devices);
    Scene[] scnArray = {scn};
    // Construct a Scene List, which we will then convert to JSON
    SceneList inpSceneList = new SceneList(registerMsgType, scnArray);
    // Send the Scene List to Crazy Ivan and get the response
    return transaction(inpSceneList);
  }

  /**
  * Create a Scene.
  */
  @Override
  public SceneList create(Scene inpScene) {
    return crudTransaction(inpScene, 0);
  }

  /**
  * Update a Scene.
  */
  @Override
  public SceneList update(Scene inpScene) {
    return crudTransaction(inpScene, 1);
  }

  /**
  * Retrieve a Scene.
  */
  @Override
  public SceneList get(String sceneName) {
    Scene scn = new Scene();
    scn.setName(sceneName);
    return crudTransaction(scn, 2);
  }

  /**
  * Remove a Scene.
  */
  @Override
  public SceneList destroy(String sceneKey) {
    Scene scn = new Scene();
    scn.setKey(sceneKey);
    return crudTransaction(scn, 3);
  }

  /**
  * Query for one or more Scenes.
  */
  @Override
  public SceneList query(Scene inpScene) {
    return crudTransaction(inpScene, 2);
  }

  /**
  * Register a Device to a Scene.
  */
  @Override
  public SceneList register(String sceneName, String deviceId, Transform inpTransform) {
    return registrationTransaction(sceneName, deviceId, inpTransform, 4);
  }

  /**
  * Deregister a Device from a Scene.
  */
  @Override
  public SceneList deregister(String sceneName, String deviceId) {
    return registrationTransaction(sceneName, deviceId, null, 5);
  }

  /**
  * Synchronize an Object and Scene transformation.
  */
  @Override
  public SceneList synchronize(String sceneName, String deviceId, Transform inpTransform) {
    return registrationTransaction(sceneName, deviceId, inpTransform, 6);
  }
}
