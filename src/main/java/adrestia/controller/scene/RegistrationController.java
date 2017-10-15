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

/**
* Rest Controller defining the Scene Registration API.
* Responsible for handling and responding to all Scene API Requests.
*/
@RestController
@RequestMapping(path = "/v1/scene")
public class RegistrationController {

  // DAO Object allowing us access to scene data
  @Autowired
  SceneDao scnData;

  // Utility Provider, providing us with basic utility methods
  @Autowired
  UtilityProviderInterface utils;

  // Scene Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.RegistrationController");

  // Process a registration request
  private ResponseEntity<Scene> processRegistration(String name,
      String device, Transform inpTransform, int registrationMsgType) {
    Scene returnScn = new Scene();
    HttpStatus returnCode = HttpStatus.OK;

    // Get a response based on the msg type we've been passed in
    SceneList ivanResponse = null;
    switch (registrationMsgType) {
      case 4: ivanResponse = scnData.register(name, device, inpTransform);
              break;
      case 5: ivanResponse = scnData.deregister(name, device);
              break;
      case 6: ivanResponse = scnData.synchronize(name, device, inpTransform);
              break;
      default: ivanResponse = null;
    }

    // If we have a successful response, then we pull the first value
    if (ivanResponse.getNumRecords() > 0
        && ivanResponse.getErrorCode() == 100) {
      returnScn = ivanResponse.getSceneList()[0];
      returnCode = utils.translateDvsError(ivanResponse.getErrorCode());
    } else {
      returnCode = HttpStatus.INTERNAL_SERVER_ERROR;
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
  * Scene Registration.
  */
  @RequestMapping(path = "/{name}/registration", method = RequestMethod.PUT)
  public ResponseEntity<Scene> register(@PathVariable("name") String name,
      @RequestParam(value = "device_id", defaultValue = "") String device,
      @RequestBody(required = false) Transform inpTransform) {
    logger.info("Responding to Scene Registration Request");
    return processRegistration(name, device, inpTransform, 4);
  }

  /**
  * Scene De-Registration.
  */
  @RequestMapping(path = "/{name}/registration", method = RequestMethod.DELETE)
  public ResponseEntity<Scene> deregister(@PathVariable("name") String name,
      @RequestParam(value = "device_id", defaultValue = "") String device) {
    logger.info("Responding to Scene De-Registration Request");
    return processRegistration(name, device, null, 5);
  }

  /**
  * Scene-Device Synchronization.
  */
  @RequestMapping(path = "/{name}/registration", method = RequestMethod.POST)
  public ResponseEntity<Scene> synchronize(@PathVariable("name") String name,
      @RequestParam(value = "device_id", defaultValue = "") String device,
      @RequestBody(required = false) Transform inpTransform) {
    logger.info("Responding to Scene Synchronization Request");
    return processRegistration(name, device, inpTransform, 6);
  }

}
