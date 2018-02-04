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
import java.util.Date;
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

  // Object Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.AssetController");

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
  @RequestMapping(path = "/v1/asset", headers=("content-type=multipart/*"), method = RequestMethod.POST)
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
      @RequestParam(value = "content-type", defaultValue = "text/plain") String contentType,
      @RequestParam(value = "file-type", defaultValue = "txt") String fileType) {
    logger.info("Responding to Asset Save Request");
    // Store the file in Mongo
    DBObject metaData = new BasicDBObject();
    metaData.put("content-type", contentType);
    metaData.put("file-type", fileType);
    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    metaData.put("created-dttm", timeStamp);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    if (file == null) {
      return new ResponseEntity<String>("Failure", responseHeaders, HttpStatus.BAD_REQUEST);
    }
    String newId = "";
    try {
      newId =
        gridFsTemplate.store(file.getInputStream(), metaData).getId().toString();
    } catch (Exception e) {
      logger.error("Error Saving Asset to Mongo: ", e);
      return new ResponseEntity<String>("Failure", responseHeaders,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<String>(newId, responseHeaders, HttpStatus.OK);
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
