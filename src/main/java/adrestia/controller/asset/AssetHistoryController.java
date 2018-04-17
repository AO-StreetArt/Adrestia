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

package adrestia.controller.asset;

import adrestia.dao.asset.AssetHistoryRepository;
import adrestia.model.asset.AssetHistory;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* Rest Controller defining the Asset API.
* Responsible for handling and responding to all Asset API Requests.
*/
@Controller
public class AssetHistoryController {

  // Spring Data Mongo Repository allowing access to standard Mongo operations
  @Autowired
  AssetHistoryRepository assetHistories;

  // Asset Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.AssetController");

  /**
  * Retrieve an Asset History.
  */
  @GetMapping("/v1/asset-history/{assetId}")
  @ResponseBody
  public ResponseEntity<List<AssetHistory>> getHistory(@PathVariable String assetId) {
    logger.info("Responding to Asset History Get Request");
    HttpStatus returnCode = HttpStatus.OK;
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");
    List<AssetHistory> existingHistoryList = assetHistories.findByAsset(assetId);
    if (existingHistoryList.size() == 0) {
      returnCode = HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
      logger.debug("No Asset Histories found");
    }

    // Create and return the new HTTP Response
    return new ResponseEntity<List<AssetHistory>>(existingHistoryList, responseHeaders, returnCode);
  }

}
