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

    System.out.println("HTTP Success Code: ");
    System.out.println(response.getStatusCode());
    assert (response.getStatusCode().is2xxSuccessful());
  }

  // Test basic CRUD Functions for scenes
  @Test
  public void testSceneCrudApi() throws Exception {
    // Create Test
    // Build a new scene
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
    Scene scn = new Scene("A1B2C3D", "", "C", 1.0, 2.0, 3.0, assets, tags, devices);
    // Post the Scene to the endpoint
    ResponseEntity<Map> create_response = this.test_template.postForEntity(
    "http://localhost:" + this.port + "/v1/scene/AeselTestScene1", scn, Map.class);
    // Read the response
    System.out.println("HTTP Success Code: ");
    System.out.println(create_response.getStatusCode());
    assert (create_response.getStatusCode().is2xxSuccessful());

    // Get Test
    // Issue a get request for the scene just created
    ResponseEntity<Map> get_response = this.test_template.getForEntity(
    "http://localhost:" + this.port + "/v1/scene/AeselTestScene1", Map.class);

    System.out.println("HTTP Success Code: ");
    System.out.println(get_response.getStatusCode());
    assert (get_response.getStatusCode().is2xxSuccessful());

    // Update Test
    Scene scn2 = new Scene("", "", "NewRegion", 110.0, 210.0, 3.0, assets, tags, devices);
    // Post the Scene to the endpoint
    ResponseEntity<Map> update_response = this.test_template.postForEntity(
    "http://localhost:" + this.port + "/v1/scene/AeselTestScene1", scn2, Map.class);
    // Read the response
    System.out.println("HTTP Success Code: ");
    System.out.println(update_response.getStatusCode());
    assert (update_response.getStatusCode().is2xxSuccessful());
  }
}
