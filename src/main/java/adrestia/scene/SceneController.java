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

import java.util.concurrent.atomic.AtomicLong;
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

/**
* Rest Controller defining the Scene API
* Responsible for handling and responding to all Scene API Requests
*/
@RestController
public class SceneController {

  @Autowired
  org.springframework.cloud.client.discovery.DiscoveryClient consul_client;

  /**
	* Scene Retrieval
  * Scene name input as path variable, no Request Parameters accepted
	*/
  @RequestMapping(path = "/v1/scene/{name}", method = RequestMethod.GET)
  public ResponseEntity<Scene> get_scene(@PathVariable("name") String name) {
    // Find an instance of CrazyIvan
    // Need to address Issue #59 in Crazy Ivan in order to uncomment
    // org.springframework.cloud.client.ServiceInstance serviceInstance =
	  // consul_client.getInstances("CrazyIvan")
	  //       .stream()
	  //       .findFirst()
	  //       .orElseThrow(() -&amp;gt; new RuntimeException("CrazyIvan instance not found"));
    // Build a new scene
    String[] assets;
    assets = new String[2];
    assets[0] = "1";
    assets[1] = "2";
    String[] tags;
    tags = new String[2];
    tags[0] = "1";
    tags[1] = "2";
    Scene scn = new Scene("A", name, "C", 1.0, 2.0, 3.0, assets, tags);

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(scn, responseHeaders, HttpStatus.OK);
  }

  /**
	* Scene Create/Update
  * Scene name input as path variable, no Request Parameters accepted
  * POST Data read in with scene data
	*/
  @RequestMapping(path = "v1/scene/{name}", headers="Content-Type=application/json", method = RequestMethod.POST)
  public ResponseEntity<Scene> update_scene(@PathVariable("name") String name, @RequestBody Scene inp_scene) {
    String region = "Bad";
    if (inp_scene != null) {
      region = inp_scene.getRegion();
    }
    // Build a new scene
    String[] assets;
    assets = new String[2];
    assets[0] = "1";
    assets[1] = "2";
    String[] tags;
    tags = new String[2];
    tags[0] = "1";
    tags[1] = "2";
    Scene scn = new Scene("A", name, region, 1.0, 2.0, 3.0, assets, tags);

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    return new ResponseEntity<Scene>(scn, responseHeaders, HttpStatus.OK);
  }

  /**
	* Scene Delete
  * Scene name input as path variable, no Request Parameters accepted
	*/
  @RequestMapping(path = "/v1/scene/{name}", method = RequestMethod.DELETE)
  public ResponseEntity<Scene> delete_scene(@PathVariable("name") String name) {
    // Build a new scene
    String[] assets;
    assets = new String[2];
    assets[0] = "1";
    String[] tags;
    tags = new String[2];
    tags[0] = "1";
    Scene scn = new Scene("A", name, "C", 1.0, 2.0, 3.0, assets, tags);

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    // Create and return the new HTTP Response
    return new ResponseEntity<Scene>(scn, responseHeaders, HttpStatus.OK);
  }

  /**
	* Scene Query
  * No Request Parameters accepted
  * POST Data read in with scene data
	*/
  @RequestMapping(path = "v1/scene/data", headers="Content-Type=application/json", method = RequestMethod.POST)
  public ResponseEntity<SceneList> query_scene(@RequestBody Scene inp_scene) {
    String region = "Bad";
    if (inp_scene != null) {
      region = inp_scene.getRegion();
    }
    // Build a new scene
    String[] assets;
    assets = new String[2];
    assets[0] = "1";
    assets[1] = "2";
    String[] tags;
    tags = new String[2];
    tags[0] = "1";
    tags[1] = "2";
    Scene scn = new Scene("A", "B", region, 1.0, 2.0, 3.0, assets, tags);
    Scene[] scn_array = new Scene[1];
    scn_array[0] = scn;

    // Build a Scene List
    SceneList list = new SceneList(0, 1, scn_array);

    // Set up a response header to return a valid HTTP Response
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Content-Type", "application/json");

    return new ResponseEntity<SceneList>(list, responseHeaders, HttpStatus.OK);
  }
}
