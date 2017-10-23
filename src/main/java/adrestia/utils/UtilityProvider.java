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

import java.lang.system;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Random;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


/**
* Adrestia Utility Provider.
* Provides access to various utility methods.
*/
@Component
public class UtilityProvider implements UtilityProviderInterface {

  Random r = null;

  /**
  * Default empty UtilityProvider constructor.
  */
  public UtilityProvider() {
    super();
    // Start the random number generator with a seed of the current time
    r = new Random(System.currentTimeMillis());
  }

  /**
  * Destroy the Utility Provider.
  */
  @PreDestroy
  public void destroy() {
    // Nothing needed yet
  }

  /**
  * Translate an Error Code from Crazy Ivan into an HTTP Error Code.
  */
  @Override
  public HttpStatus translateDvsError(int dvsErrorCode) {
    HttpStatus returnCode;
    switch (dvsErrorCode) {
      // Success Return Code
      case 100: returnCode = HttpStatus.OK;
      break;
      // Not Found
      case 102: returnCode = HttpStatus.NO_CONTENT;
      break;
      // Translation Error
      case 110: returnCode = HttpStatus.BAD_REQUEST;
      break;
      // Insufficient Data Error
      case 122: returnCode = HttpStatus.NOT_ACCEPTABLE;
      break;
      default: returnCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return returnCode;
  }

  @Override
  public int getRandomInt(int max) {
    return r.nextInt(max);
  }

}
