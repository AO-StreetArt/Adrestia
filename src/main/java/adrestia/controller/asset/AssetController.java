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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
* Rest Controller defining the Asset API.
* Responsible for handling and responding to all Asset API Requests.
*/
@Controller
public class AssetController {

  // Spring-Data Object allowing access to Mongo GridFS
  @Autowired
  GridFsTemplate gridFsTemplate;

  // Spring Data Mongo Repository allowing access to standard Mongo operations
  @Autowired
  AssetHistoryRepository assetHistories;

  // DAO Object allowing access to scene data
  @Autowired
  SceneDao scnData;

  // DAO Object allowing access to object data
  @Autowired
  ObjectDao objData;

  // Utility Provider, providing us with basic utility methods
  @Autowired
  UtilityProviderInterface utils;

  // Object Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.AssetController");

  private String saveAsset(MultipartFile file, String contentType, String fileType) {
    DBObject metaData = new BasicDBObject();
    metaData.put("content-type", contentType);
    metaData.put("file-type", fileType);
    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    metaData.put("created-dttm", timeStamp);
    String newId = "";
    if (file == null) {
      return newId;
    }
    try {
      newId =
        gridFsTemplate.store(file.getInputStream(), metaData).getId().toString();
    } catch (Exception e) {
      logger.error("Error Saving Asset to Mongo: ", e);
    }
    return newId;
  }

  private Scene findScene(String sceneName) {
    Scene retScene = null;
    SceneList ivanResponse = scnData.get(sceneName);
    if (ivanResponse.getNumRecords() > 0
        && ivanResponse.getErrorCode() == 100) {
      logger.debug("Existing Scene found in Crazy Ivan");
      // Set the key on the input scene to the key from the response
      retScene = ivanResponse.getSceneList()[0];
    }
    return retScene;
  }

  private Scene buildUpdateScene(String key, String assetId) {
    Scene updateScn = new Scene();
    // Update the scene
    updateScn.setKey(key);
    String[] newAssets = new String[1];
    newAssets[0] = assetId;
    updateScn.setAssets(newAssets);
    return updateScn;
  }

  private HttpStatus executeUpdate(Scene inpScene, boolean isAppendOperation) {
    HttpStatus returnCode = HttpStatus.OK;
    SceneList updateResponse = scnData.update(inpScene, isAppendOperation);
    if (updateResponse.getNumRecords() > 0
        && updateResponse.getErrorCode() == 100) {
      returnCode = utils.translateDvsError(updateResponse.getErrorCode());
    } else {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("Failure Registered.  Ivan Response Error Code and Length:");
      logger.debug(updateResponse.getNumRecords());
      logger.debug(updateResponse.getErrorCode());
    }
    return returnCode;
  }

  private void updateAssetHistory(String sceneName, String objectName,
      String assetId, String oldAssetId) {
    List<AssetHistory> existingHistoryList = assetHistories.findByAsset(oldAssetId);
    // If we have an existing history, update it.
    if (existingHistoryList.size() > 0) {
      AssetHistory existingHistory = existingHistoryList.get(0);
      existingHistory.getAssetIds().add(0, assetId);
      existingHistory.setAsset(assetId);
      assetHistories.save(existingHistory);
    } else {
      // If we don't have an existing history, create one
      List<String> historyList = new ArrayList<String>();
      historyList.add(oldAssetId);
      historyList.add(0, assetId);
      AssetHistory newHistory = new AssetHistory();
      newHistory.setScene(sceneName);
      newHistory.setObject(objectName);
      newHistory.setAsset(assetId);
      newHistory.setAssetIds(historyList);
      assetHistories.save(newHistory);
    }
  }

  private HttpStatus overwriteAssetToScene(String sceneName, String assetId, String oldAssetId) {
    HttpStatus returnCode = HttpStatus.OK;
    // See if we can find the scene requested
    Scene inpScene = findScene(sceneName);
    if (inpScene != null) {
      logger.debug("Existing Scene found in Crazy Ivan");
      // Set the key on the input scene to the key from the response
      String ivanRespKey = inpScene.getKey();
      if (ivanRespKey != null && !ivanRespKey.isEmpty()) {
        logger.debug("Ivan Response Key: " + ivanRespKey);
        // Add the new property before removing the old one
        Scene updateScn = buildUpdateScene(ivanRespKey, assetId);
        SceneList updateResponse = scnData.update(updateScn, true);
        if (updateResponse.getNumRecords() > 0
            && updateResponse.getErrorCode() == 100) {
          Scene assetRemovalScene = buildUpdateScene(ivanRespKey, oldAssetId);
          returnCode = executeUpdate(assetRemovalScene, false);
        } else {
          returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
          logger.debug("Failure Registered.  Ivan Response Error Code and Length:");
          logger.debug(updateResponse.getNumRecords());
          logger.debug(updateResponse.getErrorCode());
        }
      }
    } else {
      logger.debug("Failure response from Crazy Ivan");
    }
    return returnCode;
  }

  private HttpStatus addAssetToScene(String sceneName, String assetId) {
    HttpStatus returnCode = HttpStatus.OK;
    // See if we can find the scene requested
    Scene inpScene = findScene(sceneName);
    if (inpScene != null) {
      logger.debug("Existing Scene found in Crazy Ivan");
      // Set the key on the input scene to the key from the response
      String ivanRespKey = inpScene.getKey();
      if (ivanRespKey != null && !ivanRespKey.isEmpty()) {
        logger.debug("Ivan Response Key: " + ivanRespKey);
        Scene updateScn = buildUpdateScene(ivanRespKey, assetId);
        returnCode = executeUpdate(updateScn, true);
      }
    } else {
      logger.debug("Failure response from Crazy Ivan");
    }
    return returnCode;
  }

  private ObjectDocument findObject(String sceneName, String objectName) {
    ObjectDocument queryObj = new ObjectDocument();
    queryObj.setName(objectName);
    queryObj.setOwner(null);
    queryObj.setScene(sceneName);
    ObjectList clymanResponse = objData.query(queryObj);;

    // If we have a successful response, then the Object exists
    if (clymanResponse.getNumRecords() > 0
        && clymanResponse.getErrorCode() == 100) {
      return clymanResponse.getDocuments()[0];
    }
    return null;
  }

  private HttpStatus executeObjectUpdate(ObjectDocument updateObj, boolean isAppendOperation) {
    HttpStatus returnCode = HttpStatus.OK;
    ObjectList updateResponse = objData.update(updateObj, isAppendOperation);
    returnCode = utils.translateDvsError(updateResponse.getErrorCode());
    if (updateResponse.getNumRecords() <= 0
        && updateResponse.getErrorCode() != 100) {
      returnCode = HttpStatus.INTERNAL_SERVER_ERROR;
      logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
      logger.debug(updateResponse.getNumRecords());
      logger.debug(updateResponse.getErrorCode());
    }
    return returnCode;
  }

  private ObjectDocument buildUpdateObject(String inpKey, String assetId, String clymanRespOwner) {
    ObjectDocument updateObj = new ObjectDocument();
    updateObj.setKey(inpKey);
    String[] newAssets = new String[1];
    newAssets[0] = assetId;
    updateObj.setAssets(newAssets);
    updateObj.setOwner(clymanRespOwner);
    return updateObj;
  }

  private HttpStatus overwriteAssetToObject(String sceneName, String objectName,
      String assetId, String oldAssetId) {
    HttpStatus returnCode = HttpStatus.OK;
    // See if we can find the Object requested
    ObjectDocument existingDoc = findObject(sceneName, objectName);
    if (existingDoc != null) {
      logger.debug("Existing Object found in Clyman");
      // Set the key on the input Object to the key from the response
      String clymanRespKey = existingDoc.getKey();
      String clymanRespOwner = existingDoc.getOwner();
      if (clymanRespKey != null && !clymanRespKey.isEmpty()) {
        logger.debug("Clyman Response Key: " + clymanRespKey);
        ObjectDocument updateObj = buildUpdateObject(clymanRespKey, assetId, clymanRespOwner);
        ObjectList updateResponse = objData.update(updateObj, true);
        returnCode = utils.translateDvsError(updateResponse.getErrorCode());
        if (updateResponse.getNumRecords() <= 0
            && updateResponse.getErrorCode() != 100) {
          returnCode = HttpStatus.INTERNAL_SERVER_ERROR;
          logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
          logger.debug(updateResponse.getNumRecords());
          logger.debug(updateResponse.getErrorCode());
        } else {
          // Execute a secondary update to remove the original asset from the scene
          ObjectDocument removalUpdateObj = buildUpdateObject(clymanRespKey, oldAssetId);
          returnCode = executeObjectUpdate(removalUpdateObj, false);
        }
      }
    } else {
      logger.debug("Failure response from Clyman");
    }
    return returnCode;
  }

  private HttpStatus addAssetToObject(String sceneName, String objectName, String assetId) {
    HttpStatus returnCode = HttpStatus.OK;
    // See if we can find the Object requested
    ObjectDocument existingDoc = findObject(sceneName, objectName);
    if (existingDoc != null) {
      logger.debug("Existing Object found in Clyman");
      // Set the key on the input Object to the key from the response
      String clymanRespKey = existingDoc.getKey();
      if (clymanRespKey != null && !clymanRespKey.isEmpty()) {
        logger.debug("Clyman Response Key: " + clymanRespKey);
        ObjectDocument updateObj = buildUpdateObject(clymanRespKey, assetId);
        returnCode = executeObjectUpdate(updateObj, true);
      }
    } else {
      logger.debug("Failure response from Clyman");
    }
    return returnCode;
  }

  /**
  * Retrieve an Asset.
  */
  @GetMapping("/v1/asset/{key}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String key) throws MalformedURLException {
    logger.info("Responding to Asset Get Request");
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    // Load the file from Mongo
    GridFSDBFile gridFsdbFile;
    try {
      gridFsdbFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(key)));
    } catch (Exception e) {
      logger.error("Error Retrieving Asset from Mongo: ", e);
      return new ResponseEntity<Resource>(new UrlResource("http://server.error"), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (gridFsdbFile == null) {
      logger.error("Null Asset Retrieved from Mongo");
      return new ResponseEntity<Resource>(new UrlResource("http://server.error"), responseHeaders, HttpStatus.NO_CONTENT);
    }
    InputStreamResource fileResource = new InputStreamResource(gridFsdbFile.getInputStream());
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + key + "\"").body(fileResource);
  }

  /**
  * Create an Asset.
  * Uses Multi-part form data to accept the file
  */
  @RequestMapping(path = "/v1/asset",
      headers = ("content-type=multipart/*"),
      method = RequestMethod.POST)
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
      @RequestParam(value = "content-type", defaultValue = "text/plain") String contentType,
      @RequestParam(value = "file-type", defaultValue = "txt") String fileType,
      @RequestParam(value = "scene", defaultValue = "") String sceneName,
      @RequestParam(value = "object", defaultValue = "") String objectName) {
    logger.info("Responding to Asset Save Request");
    HttpStatus returnCode = HttpStatus.OK;
    String newId = saveAsset(file, contentType, fileType);
    HttpHeaders responseHeaders = new HttpHeaders();
    if (newId.isEmpty()) {
      return new ResponseEntity<String>("Failure", responseHeaders,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (!sceneName.isEmpty()) {
      if (!objectName.isEmpty()) {
        logger.info("Updating Object with new Asset");
        returnCode = addAssetToObject(sceneName, objectName, newId);
      } else {
        logger.info("Updating Scene with new Asset");
        returnCode = addAssetToScene(sceneName, newId);
      }
    }
    return new ResponseEntity<String>(newId, responseHeaders, returnCode);
  }

  /**
  * Update an Asset.
  * Uses Multi-part form data to accept the file
  */
  @RequestMapping(path = "/v1/asset/{key}",
      headers = ("content-type=multipart/*"),
      method = RequestMethod.POST)
  public ResponseEntity<String> handleFileUpdate(@PathVariable String key,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "content-type", defaultValue = "text/plain") String contentType,
      @RequestParam(value = "file-type", defaultValue = "txt") String fileType,
      @RequestParam(value = "scene", defaultValue = "") String sceneName,
      @RequestParam(value = "object", defaultValue = "") String objectName) {
    HttpStatus returnCode = HttpStatus.OK;
    String newId = saveAsset(file, contentType, fileType);
    HttpHeaders responseHeaders = new HttpHeaders();
    if (newId.isEmpty()) {
      return new ResponseEntity<String>("Failure", responseHeaders,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (!sceneName.isEmpty()) {
      if (!objectName.isEmpty()) {
        logger.info("Updating Object with new Asset");
        returnCode = overwriteAssetToObject(sceneName, objectName, newId, key);
      } else {
        logger.info("Updating Scene with new Asset");
        returnCode = overwriteAssetToScene(sceneName, newId, key);
      }
    }
    updateAssetHistory(sceneName, objectName, newId, key);
    return new ResponseEntity<String>(newId, responseHeaders, returnCode);
  }

  /**
  * Delete an Asset.
  */
  @RequestMapping(path = "/v1/asset/{key}",
      method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteFile(@PathVariable String key) {
    logger.info("Responding to Asset Delete Request");
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    // Delete the file from Mongo
    try {
      gridFsTemplate.delete(new Query(Criteria.where("_id").is(key)));
    } catch (Exception e) {
      logger.error("Error Deleting Asset from Mongo: ", e);
      return new ResponseEntity<String>("Failure", responseHeaders,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<String>("Success", responseHeaders, HttpStatus.OK);
  }

}
