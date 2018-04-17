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

package adrestia.controller.obj3;

import adrestia.dao.obj3.ObjectDao;
import adrestia.model.obj3.ObjectDocument;
import adrestia.model.obj3.ObjectList;
import adrestia.utils.UtilityProviderInterface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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
* Rest Controller defining the Object API.
* Responsible for handling and responding to all Object API Requests.
*/
@RestController
@RequestMapping(path = "/v1/")
public class ObjectController {

  // DAO Object allowing access to object data
  @Autowired
  ObjectDao objData;

  // Utility Provider, providing us with basic utility methods
  @Autowired
  UtilityProviderInterface utils;

  // UdpController, which exposes the UDP API
  @Autowired
  UdpController udpApi;

  // Object Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.ObjectController");

  // Save an Object to Clyman
  private ObjectList saveObject(ObjectDocument inpDoc, boolean docExists) {
    if (docExists) {
      return objData.update(inpDoc);
    } else {
      return objData.create(inpDoc);
    }
  }

  // Query Clyman
  private ObjectList objectQuery(String sceneName, String objName, int objFrame) {
    // Execute a query against Clyman
    ObjectDocument queryObj = new ObjectDocument();
    queryObj.setName(objName);
    queryObj.setScene(sceneName);
    queryObj.setFrame(objFrame);
    return objData.query(queryObj);
  }

  // Query Clyman
  private ObjectList objectQuery(String sceneName, String objName) {
    // Execute a query against Clyman
    ObjectDocument queryObj = new ObjectDocument();
    queryObj.setName(objName);
    queryObj.setScene(sceneName);
    return objData.query(queryObj);
  }

  // Determine if a response from Clyman is a response or failure
  private boolean isSuccessResponse(ObjectList clymanResponse) {
    if (clymanResponse.getNumRecords() > 0
        && clymanResponse.getErrorCode() == 100) {
      return true;
    }
    return false;
  }

  /**
  * Object Retrieval.
  * Object name & object name input as path variables, no Request Parameters accepted.
  * Add New Query Parameter for frame, 0 by default.  Include frame in
  *    CLyman messages
  */
  @RequestMapping(path = "scene/{scn_name}/object/{obj_name}", method = RequestMethod.GET)
  public ResponseEntity<ObjectDocument> getObject(@PathVariable("scn_name") String sceneName,
      @PathVariable("obj_name") String objName,
      @RequestParam(value = "frame", defaultValue = "0") int frame) {
    logger.info("Responding to Object Get Request");
    // Set up our response objects
    ObjectDocument returnObj = new ObjectDocument();
    HttpStatus returnCode = HttpStatus.OK;

    // Retrieve the object requested
    ObjectList clymanResponse = objectQuery(sceneName, objName, frame);

    // If we have a successful response, then we pull the first value and
    // the error code
    returnCode = utils.translateDvsError(clymanResponse.getErrorCode());
    if (clymanResponse.getNumRecords() > 0) {
      returnObj = clymanResponse.getDocuments()[0];
    } else if (clymanResponse.getErrorCode() == 100) {
      logger.debug("Query returned no values");
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;;
    } else {
      logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
      logger.debug(clymanResponse.getNumRecords());
      logger.debug(clymanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<ObjectDocument>(returnObj, responseHeaders, returnCode);
  }

  /**
  * Object Create/Update.
  * Object Name & Object name input as path variable, no Request Parameters accepted.
  * POST Data read in with Object data.
  * If we are updating, update only the doc with a matching frame.
  *    if we don't have a frame, set to frame 0
  */
  @RequestMapping(path = "scene/{scn_name}/object/{obj_name}",
      headers = "Content-Type=application/json",
      method = RequestMethod.POST)
  public ResponseEntity<ObjectDocument> updateObject(
      @PathVariable("scn_name") String sceneName,
      @PathVariable("obj_name") String objName,
      @RequestParam(value = "frame", defaultValue = "0") int frame,
      @RequestBody ObjectDocument inpObject) {
    logger.info("Responding to Object Save Request");
    ObjectDocument returnObj = new ObjectDocument();

    // See if we can find the Object requested
    ObjectList clymanResponse = objectQuery(sceneName, objName, frame);

    // If we have a successful response, then the Object exists
    boolean objectExists = false;
    if (isSuccessResponse(clymanResponse)) {
      objectExists = true;
      logger.debug("Existing Object found in Clyman");
      // Set the key on the input Object to the key from the response
      String clymanRespKey = clymanResponse.getDocuments()[0].getKey();
      if (clymanRespKey != null && !clymanRespKey.isEmpty()) {
        inpObject.setKey(clymanRespKey);
        logger.debug("Clyman Response Key: " + clymanRespKey);
      }
    }

    // Update the Object
    inpObject.setName(objName);
    inpObject.setScene(sceneName);
    inpObject.setFrame(frame);
    ObjectList updateResponse = saveObject(inpObject, objectExists);

    // If we have a successful response, then we pull the first value
    HttpStatus returnCode = utils.translateDvsError(updateResponse.getErrorCode());
    if (isSuccessResponse(updateResponse)) {
      returnObj = updateResponse.getDocuments()[0];
    } else {
      logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
      logger.debug(updateResponse.getNumRecords());
      logger.debug(updateResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<ObjectDocument>(returnObj, responseHeaders, returnCode);
  }

  /**
  * Object Overwrite.
  * Object key input as path variable, no Request Parameters accepted.
  * POST Data read in with Object data.
  */
  @RequestMapping(path = "object/{obj_key}",
      headers = "Content-Type=application/json",
      method = RequestMethod.POST)
  public ResponseEntity<ObjectDocument> overwriteObject(
      @PathVariable("obj_key") String objectKey,
      @RequestBody ObjectDocument inpObject) {
    logger.info("Responding to Object Overwrite Request");
    ObjectDocument returnObj = new ObjectDocument();
    HttpStatus returnCode = HttpStatus.OK;

    // Send the update
    inpObject.setKey(objectKey);
    ObjectList clymanResponse = objData.overwrite(inpObject);
    // If we have a successful response, then we pull the first value
    returnCode = utils.translateDvsError(clymanResponse.getErrorCode());
    if (isSuccessResponse(clymanResponse)) {
      returnObj = clymanResponse.getDocuments()[0];
    } else {
      logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
      logger.debug(clymanResponse.getNumRecords());
      logger.debug(clymanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<ObjectDocument>(returnObj, responseHeaders, returnCode);
  }

  /**
  * Object Delete.
  * Object Name & Object name input as path variable, no Request Parameters accepted.
  */
  @RequestMapping(path = "scene/{scn_name}/object/{obj_name}",
      method = RequestMethod.DELETE)
  public ResponseEntity<ObjectDocument> deleteObject(
      @PathVariable("scn_name") String sceneName,
      @PathVariable("obj_name") String objName,
      @RequestParam(value = "frame", defaultValue = "-1") int frame) {
    logger.info("Responding to Object Delete Request");
    ObjectDocument returnObj = new ObjectDocument();
    HttpStatus returnCode = HttpStatus.OK;

    // See if we can find the Object requested
    ObjectList clymanResponse;
    if (frame == -1) {
      clymanResponse = objectQuery(sceneName, objName);
    } else {
      clymanResponse = objectQuery(sceneName, objName, frame);
    }

    // If we have a successful response, then the Object(s) exist
    if (isSuccessResponse(clymanResponse)) {
      for (int i = 0; i < clymanResponse.getDocuments().length; i++) {
        String clymanRespKey = clymanResponse.getDocuments()[i].getKey();
        if (clymanRespKey != null && !clymanRespKey.isEmpty()) {
          logger.debug("Clyman Response Key: " + clymanRespKey);
          ObjectList deleteResponse = objData.destroy(clymanRespKey);
          returnCode = utils.translateDvsError(deleteResponse.getErrorCode());
          // If we have a successful response, then set a success code
          if (isSuccessResponse(deleteResponse)) {
            returnObj = deleteResponse.getDocuments()[0];
          } else {
            logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
            logger.debug(deleteResponse.getNumRecords());
            logger.debug(deleteResponse.getErrorCode());
          }
        } else {
          logger.error("Unable to find key in clyman response");
        }
      }
    } else {
      // Delete request for non-existing object
      returnCode = HttpStatus.INTERNAL_SERVER_ERROR;
      logger.debug("Document not found");
      logger.debug(clymanResponse.getNumRecords());
      logger.debug(clymanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<ObjectDocument>(returnObj, responseHeaders, returnCode);
  }

  /**
  * Object Query.
  * Scene name input as path variable, Request Parameters accepted.
  */
  @RequestMapping(method = RequestMethod.GET, path = "scene/{scn_name}/object")
  public ResponseEntity<ObjectList> queryObject(
      @PathVariable("scn_name") String sceneName,
      @RequestParam(value = "type", defaultValue = "") String type,
      @RequestParam(value = "subtype", defaultValue = "") String subtype,
      @RequestParam(value = "owner", defaultValue = "") String owner,
      @RequestParam(value = "frame", defaultValue = "-9999") int frame) {
    logger.info("Responding to Object Query");

    // Execute a query against Clyman
    ObjectDocument queryObj = new ObjectDocument();
    queryObj.setScene(sceneName);
    if (!(type.isEmpty())) {
      queryObj.setType(type);
    }
    if (!(subtype.isEmpty())) {
      queryObj.setSubtype(subtype);
    }
    if (!(owner.isEmpty())) {
      queryObj.setOwner(owner);
    }
    if (frame != -9999) {
      queryObj.setFrame(frame);
    }
    ObjectList clymanResponse = objData.query(queryObj);

    // Update our HTTP Response based on the Clyman response
    HttpStatus returnCode = utils.translateDvsError(clymanResponse.getErrorCode());
    if (!(isSuccessResponse(clymanResponse))) {
      logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
      logger.debug(clymanResponse.getNumRecords());
      logger.debug(clymanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<ObjectList>(clymanResponse, responseHeaders, returnCode);
  }

  private ResponseEntity<ObjectDocument> lockTransaction(String sceneName,
      String objName, String owner, boolean isLocking) {
    logger.info("Object Lock Transaction");
    ObjectDocument returnObj = new ObjectDocument();
    HttpStatus returnCode = HttpStatus.OK;

    // Execute a query against Clyman with a frame of 9.
    ObjectDocument queryObj = new ObjectDocument();
    queryObj.setScene(sceneName);
    queryObj.setName(objName);
    queryObj.setOwner(null);
    queryObj.setFrame(0);
    ObjectList clymanResponse = objData.query(queryObj);

    if (isSuccessResponse(clymanResponse)) {
      // Execute the lock transaction
      ObjectList lockResponse = null;
      if (isLocking) {
        lockResponse = objData.lock(clymanResponse.getDocuments()[0].getKey(), owner);
      } else {
        lockResponse = objData.unlock(clymanResponse.getDocuments()[0].getKey(), owner);
      }
      returnCode = utils.translateDvsError(lockResponse.getErrorCode());
      if (isSuccessResponse(lockResponse)) {
        returnObj = lockResponse.getDocuments()[0];
      } else {
        logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
        logger.debug(clymanResponse.getNumRecords());
        logger.debug(clymanResponse.getErrorCode());
      }
    } else {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
      logger.debug(clymanResponse.getNumRecords());
      logger.debug(clymanResponse.getErrorCode());
    }

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<ObjectDocument>(returnObj, responseHeaders, returnCode);
  }

  /**
  * Object Lock.
  * Object Name & Scene name input as path variable, Request Parameters accepted.
  */
  @RequestMapping(path = "scene/{scn_name}/object/{obj_name}/lock",
      method = RequestMethod.GET)
  public ResponseEntity<ObjectDocument> lockObject(
      @PathVariable("scn_name") String sceneName,
      @PathVariable("obj_name") String objName,
      @RequestParam(value = "owner", defaultValue = "") String owner) {
    return lockTransaction(sceneName, objName, owner, true);
  }

  /**
  * Object Unlock.
  * Object Name & Scene name input as path variable, Request Parameters accepted.
  */
  @RequestMapping(path = "scene/{scn_name}/object/{obj_name}/lock",
      method = RequestMethod.DELETE)
  public ResponseEntity<ObjectDocument> unlockObject(
      @PathVariable("scn_name") String sceneName,
      @PathVariable("obj_name") String objName,
      @RequestParam(value = "owner", defaultValue = "") String owner) {
    return lockTransaction(sceneName, objName, owner, false);
  }
}
