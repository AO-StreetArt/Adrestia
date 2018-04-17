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

package adrestia.dao.scene;

import adrestia.dao.zmq.ZmqConnection;
import adrestia.model.scene.Scene;
import adrestia.model.scene.SceneList;
import adrestia.model.scene.Transform;
import adrestia.model.scene.UserDevice;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
* Dao Implementation for Scene Objects using Crazy Ivan.
*/
@Component
public class CrazyIvanConnector implements SceneDao {

  @Autowired
  ZmqConnection zmqConn;

  // How many retries should we attempt prior to reporting a failure
  @Value("${server.zmq.retries}")
  private int requestRetries;
  // How many milliseconds to wait for a reply
  @Value("${server.zmq.timeout}")
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
          zmqConn.send(ivanMsg, requestTimeout, requestRetries, "Ivan");
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
  private SceneList buildSceneList(Scene inpScene, int msgType, int opType) {
    Scene[] baseInpScns = {inpScene};
    SceneList scnList = new SceneList(msgType, baseInpScns);
    scnList.setOpType(opType);
    return scnList;
  }

  // Execute a CRUD Transaction with Crazy Ivan
  private SceneList crudTransaction(Scene inpScene, int msgType, int opType) {
    // Construct a Scene List, which we will then convert to JSON
    SceneList inpSceneList = buildSceneList(inpScene, msgType, opType);
    // Send the Scene List to Crazy Ivan and get the response
    return transaction(inpSceneList);
  }

  /**
  * Create a Scene.
  */
  @Override
  public SceneList create(Scene inpScene) {
    return crudTransaction(inpScene, 0, 10);
  }

  /**
  * Update a Scene.
  */
  @Override
  public SceneList update(Scene inpScene) {
    return update(inpScene, true);
  }

  /**
  * Update a Scene.
  * @param inpScene A Scene Object to save
  * @param isAppendOperation True if we want to send an append operation to Crazy Ivan, false to send a remove operation
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  @Override
  public SceneList update(Scene inpScene, boolean isAppendOperation) {
    if (isAppendOperation) {
      return crudTransaction(inpScene, 1, 10);
    }
    return crudTransaction(inpScene, 1, 11);
  }

  /**
  * Retrieve a Scene.
  */
  @Override
  public SceneList get(String sceneName) {
    Scene scn = new Scene();
    scn.setName(sceneName);
    return crudTransaction(scn, 2, 10);
  }

  /**
  * Remove a Scene.
  */
  @Override
  public SceneList destroy(String sceneKey) {
    Scene scn = new Scene();
    scn.setKey(sceneKey);
    return crudTransaction(scn, 3, 10);
  }

  /**
  * Query for one or more Scenes.
  */
  @Override
  public SceneList query(Scene inpScene) {
    return crudTransaction(inpScene, 2, 10);
  }

  // Execute a Registration Transaction with Crazy Ivan
  private SceneList registrationTransaction(String sceneName,
      String deviceId, Transform inpTransform, int registerMsgType) {
    return registrationTransaction(sceneName, deviceId, "", 999999,
        inpTransform, registerMsgType);
  }

  // Execute a Registration Transaction with Crazy Ivan
  private SceneList registrationTransaction(String sceneName,
      String deviceId, String deviceHost, int devicePort,
      Transform inpTransform, int registerMsgType) {
    logger.debug("Scene Registration Name: " + sceneName);
    logger.debug("Scene Registration Device " + deviceId);

    // Construct a User Device array
    UserDevice ud = new UserDevice();
    ud.setKey(deviceId);
    ud.setHost(deviceHost);
    if (devicePort < 999999) {
      ud.setPort(devicePort);
    }
    // Pass the transform to the user device, if one is passed in
    if (inpTransform != null) {
      logger.debug("Input Transform detected");
      ud.setTransform(inpTransform);
    } else {
      // We can't pass a null value to Crazy Ivan due to a bug
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotation = {0.0, 0.0, 0.0};
      Transform newTransform = new Transform(translation, rotation);
      ud.setTransform(newTransform);
    }
    UserDevice[] devices = {ud};
    // Construct a scene
    Scene scn = new Scene();
    scn.setName(sceneName);
    scn.setDevices(devices);
    // Get any existing scenes
    SceneList existingScenes = get(sceneName);
    if (existingScenes.getErrorCode() == 100) {
      // We have an existing scene, use the key for it
      scn.setKey(existingScenes.getSceneList()[0].getKey());
      logger.debug("Found existing scene: " + existingScenes.getSceneList()[0].getKey());
    } else {
      logger.debug("Processing Registration without existing scene");
    }

    // Construct a Scene List, which we will then convert to JSON
    Scene[] scnArray = {scn};
    SceneList inpSceneList = new SceneList(registerMsgType, scnArray);
    // Send the Scene List to Crazy Ivan and get the response
    return transaction(inpSceneList);
  }

  /**
  * Register a Device to a Scene.
  */
  @Override
  public SceneList register(String sceneName, String deviceId, Transform inpTransform) {
    return registrationTransaction(sceneName, deviceId, inpTransform, 4);
  }

  /**
  * Register a Device to a Scene with connectivity information.
  */
  @Override
  public SceneList register(String sceneName, String deviceId, String hostName,
      int portNumber, Transform inpTransform) {
    return registrationTransaction(sceneName, deviceId, hostName, portNumber, inpTransform, 4);
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
