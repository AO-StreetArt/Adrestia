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

import java.util.Map;
import java.io.PrintWriter;
import java.lang.Double;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
* Basic Tests against the Adrestia Server
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Adrestia.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=5889"})
public class BasicServerTest {
  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgt_port;

  @Autowired
  private TestRestTemplate test_template;

  // Test the Actuator Health Check endpoint, which gets hit by Consul
  @Test
  public void testActuatorHealthCheck() throws Exception {
    ResponseEntity<Map> response = this.test_template.getForEntity(
    "http://localhost:" + this.mgt_port + "/info", Map.class);

    assert (response.getStatusCode().is2xxSuccessful());
  }

  // Test basic CRUD Functions for scenes
  @Test
  public void testSceneCrudApi() throws Exception {
    String test_scene_name = "AeselTestScene9";
    String test_scene_url = "/v1/scene/" + test_scene_name;
    String test_scene_key = "A1B2C3E1";
    double TOLERANCE = 0.00001;
    // Open up a file that we can write some test results to
    // Obviously shouldn't be relied on for automated testing but good for debugging
    PrintWriter test_logger = new PrintWriter("testSceneCrudApi.txt", "UTF-8");
    try {
      test_logger.println("Starting Test for Scene CRUD API");
      // Create Test
      // Build a new scene
      test_logger.println("Create Test");
      String[] assets;
      assets = new String[2];
      assets[0] = "123";
      assets[1] = "ABC";
      String[] tags;
      tags = new String[2];
      tags[0] = "1";
      tags[1] = "2";
      UserDevice[] devices = new UserDevice[1];
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotation = {0.0, 0.0, 0.0, 0.0};
      Transform transform = new Transform(translation, rotation);
      UserDevice dev = new UserDevice("DeviceKey", transform);
      devices[0] = dev;
      Scene scn = new Scene(test_scene_key, "", "C", 1.0, 2.0, 3.0, assets, tags, devices);
      // Post the Scene to the endpoint
      ResponseEntity<Map> create_response = this.test_template.postForEntity(
      "http://localhost:" + this.port + test_scene_url, scn, Map.class);
      // Read the response
      assert (create_response.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map crt_resp_body = create_response.getBody();
      test_logger.println("Create Response: " + crt_resp_body.toString());
      test_logger.println("Key: " + crt_resp_body.get("key"));
      assert (crt_resp_body.get("key").equals(test_scene_key));

      // Get Test
      test_logger.println("Get Test");
      // Issue a get request for the scene just created
      ResponseEntity<Map> get_response = this.test_template.getForEntity(
      "http://localhost:" + this.port + test_scene_url, Map.class);
      // Read the response
      assert (get_response.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map get_resp_body = get_response.getBody();
      test_logger.println("Get Response: " + get_resp_body.toString());
      test_logger.println("Key: " + get_resp_body.get("key"));
      test_logger.println("Name: " + get_resp_body.get("name"));
      test_logger.println("Region: " + get_resp_body.get("region"));
      test_logger.println("Latitude: " + get_resp_body.get("latitude"));
      test_logger.println("Longitude: " + get_resp_body.get("longitude"));
      assert (get_resp_body.get("key").equals(test_scene_key));
      assert (get_resp_body.get("name").equals(test_scene_name));
      assert (get_resp_body.get("region").equals("C"));
      Double lat_dbl = new Double(get_resp_body.get("latitude").toString());
      assert (lat_dbl.doubleValue() - 1.0 < TOLERANCE);
      Double long_dbl = new Double(get_resp_body.get("longitude").toString());
      assert (long_dbl.doubleValue() - 2.0 < TOLERANCE);

      // Update Test
      test_logger.println("Update Test");
      Scene scn2 = new Scene("", "", "NewRegion", 110.0, 210.0, 3.0, assets, tags, devices);
      // Post the Scene to the endpoint
      ResponseEntity<Map> update_response = this.test_template.postForEntity(
      "http://localhost:" + this.port + test_scene_url, scn2, Map.class);
      // Read the response
      assert (update_response.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map upd_resp_body = update_response.getBody();
      test_logger.println("Update Response: " + upd_resp_body.toString());

      // Run a secondary get test to validate the update
      // Get Test
      test_logger.println("Get Test");
      // Issue a get request for the scene just created
      ResponseEntity<Map> get_response2 = this.test_template.getForEntity(
      "http://localhost:" + this.port + test_scene_url, Map.class);
      // Read the response
      assert (get_response2.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map get_resp_body2 = get_response2.getBody();
      test_logger.println("Get Response: " + get_resp_body2.toString());
      test_logger.println("Key: " + get_resp_body2.get("key"));
      test_logger.println("Name: " + get_resp_body2.get("name"));
      test_logger.println("Region: " + get_resp_body2.get("region"));
      test_logger.println("Latitude: " + get_resp_body2.get("latitude"));
      test_logger.println("Longitude: " + get_resp_body2.get("longitude"));
      assert (get_resp_body2.get("key").equals(test_scene_key));
      assert (get_resp_body2.get("name").equals(test_scene_name));
      assert (get_resp_body2.get("region").equals("NewRegion"));
      Double lat_dbl2 = new Double(get_resp_body2.get("latitude").toString());
      assert (lat_dbl2.doubleValue() - 110.0 < TOLERANCE);
      Double long_dbl2 = new Double(get_resp_body2.get("longitude").toString());
      assert (long_dbl2.doubleValue() - 210.0 < TOLERANCE);

      // Delete Test
    } catch (Exception e) {
      test_logger.println(e.getStackTrace());
    } finally  {
      // Close the output text file
      test_logger.close();
    }
  }
}
