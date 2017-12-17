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

import java.io.PrintWriter;
import java.lang.Double;
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
* Basic Scene Tests against the Adrestia Server.
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Adrestia.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=5889"})
public class SceneServerTest {
  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgtPort;

  @Autowired
  private TestRestTemplate testTemplate;

  // Test basic CRUD Functions for scenes
  @Test
  public void testSceneCrudApi() throws Exception {
    String testSceneName = "AeselTestScene11";
    String testSceneUrl = "/v1/scene/" + testSceneName;
    String testSceneKey = "A1B2C3E3";
    double tolerance = 0.00001;
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testSceneCrudApi.txt", "UTF-8");
    try {
      testLogger.println("Starting Test for Scene CRUD API");
      // Create Test
      // Build a new scene
      testLogger.println("Create Test");
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
      UserDevice dev = new UserDevice("DeviceKey", "TestHost", 5555, transform);
      devices[0] = dev;
      Scene scn = new Scene(
          testSceneKey, "", "C", 1.0, 2.0, 3.0, assets, tags, devices);
      // Post the Scene to the endpoint
      ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneUrl, scn, Map.class);
      testLogger.println(createResponse.getStatusCode());
      // Read the response
      assert (createResponse.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map crtRespBody = createResponse.getBody();
      testLogger.println("Create Response: " + crtRespBody.toString());
      testLogger.println("Key: " + crtRespBody.get("key"));
      assert (crtRespBody.get("key").equals(testSceneKey));

      // Get Test
      testLogger.println("Get Test");
      // Issue a get request for the scene just created
      ResponseEntity<Map> getResponse = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testSceneUrl, Map.class);
      // Read the response
      assert (getResponse.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map getRespBody = getResponse.getBody();
      testLogger.println("Get Response: " + getRespBody.toString());
      testLogger.println("Key: " + getRespBody.get("key"));
      testLogger.println("Name: " + getRespBody.get("name"));
      testLogger.println("Region: " + getRespBody.get("region"));
      testLogger.println("Latitude: " + getRespBody.get("latitude"));
      testLogger.println("Longitude: " + getRespBody.get("longitude"));
      assert (getRespBody.get("key").equals(testSceneKey));
      assert (getRespBody.get("name").equals(testSceneName));
      assert (getRespBody.get("region").equals("C"));
      Double latDbl = Double.valueOf(getRespBody.get("latitude").toString());
      assert (latDbl.doubleValue() - 1.0 < tolerance);
      Double longDbl = Double.valueOf(getRespBody.get("longitude").toString());
      assert (longDbl.doubleValue() - 2.0 < tolerance);

      // Update Test
      testLogger.println("Update Test");
      Scene scn2 = new Scene(
          "", "", "NewRegion", 110.0, 210.0, 3.0, assets, tags, devices);
      // Post the Scene to the endpoint
      ResponseEntity<Map> updateResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneUrl, scn2, Map.class);
      // Read the response
      assert (updateResponse.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map updRespBody = updateResponse.getBody();
      testLogger.println("Update Response: " + updRespBody.toString());

      // Run a secondary get test to validate the update
      testLogger.println("Get Test");
      // Issue a get request for the scene just updated
      ResponseEntity<Map> getResponse2 = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testSceneUrl, Map.class);
      // Read the response
      assert (getResponse2.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map getRespBody2 = getResponse2.getBody();
      testLogger.println("Get Response: " + getRespBody2.toString());
      testLogger.println("Key: " + getRespBody2.get("key"));
      testLogger.println("Name: " + getRespBody2.get("name"));
      testLogger.println("Region: " + getRespBody2.get("region"));
      testLogger.println("Latitude: " + getRespBody2.get("latitude"));
      testLogger.println("Longitude: " + getRespBody2.get("longitude"));
      assert (getRespBody2.get("key").equals(testSceneKey));
      assert (getRespBody2.get("name").equals(testSceneName));
      assert (getRespBody2.get("region").equals("NewRegion"));
      Double latDbl2 = Double.valueOf(getRespBody2.get("latitude").toString());
      assert (latDbl2.doubleValue() - 110.0 < tolerance);
      Double longDbl2 = Double.valueOf(getRespBody2.get("longitude").toString());
      assert (longDbl2.doubleValue() - 210.0 < tolerance);

      // Delete Test
      testLogger.println("Delete Test");
      // Delete the scene
      this.testTemplate.delete(
          "http://localhost:" + this.port + testSceneUrl, Map.class);

      // Run a tertiary get test to validate the update
      testLogger.println("Get Test");
      // Issue a get request for the scene just updated
      ResponseEntity<Map> getResponse3 = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testSceneUrl, Map.class);
      // Read the response
      testLogger.println("Delete Test Response Code");
      testLogger.println(getResponse3.getStatusCode());
      assert (getResponse3.getStatusCode().is4xxClientError());
    } catch (Exception e) {
      e.printStackTrace(testLogger);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  private void populateQueryData() {
    final String testSceneUrl = "/v1/scene/MyFirstQueryScene";
    final String testSceneUrl2 = "/v1/scene/MySecondQueryScene";
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
    UserDevice dev = new UserDevice("DeviceKey", "TestHost", 5555, transform);
    devices[0] = dev;
    Scene scn = new Scene("A4B8C12D0",
        "MyFirstQueryScene", "MyRegion", 1.0, 2.0, 3.0, assets, tags, devices);
    // Post the Scene to the endpoint
    ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
        "http://localhost:" + this.port + testSceneUrl, scn, Map.class);
    // Read the response
    assert (createResponse.getStatusCode().is2xxSuccessful());
    Scene scn2 = new Scene("A6B8C12D0", "MySecondQueryScene",
        "MyRegion", 12.0, 23.0, 34.0, assets, tags, devices);
    // Post the Scene to the endpoint
    ResponseEntity<Map> createResponse2 = this.testTemplate.postForEntity(
        "http://localhost:" + this.port + testSceneUrl2, scn2, Map.class);
    // Read the response
    assert (createResponse2.getStatusCode().is2xxSuccessful());
  }

  // Test Query Functions for scenes
  @Test
  public void testSceneQueryApi() throws Exception {
    String testSceneUrl = "/v1/scene/data";
    double tolerance = 0.00001;
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testSceneQueryApi.txt", "UTF-8");
    try {
      testLogger.println("Populating Data for Query Tests");
      populateQueryData();
      // Run Query Tests
      testLogger.println("Starting Scene Query Tests");
      // Build a new scene
      testLogger.println("Query Test");
      String[] assets = new String[0];
      String[] tags = new String[0];
      UserDevice[] devices = new UserDevice[0];
      Scene scn = new Scene(
          "", "", "MyRegion", -9999.0, -9999.0, -9999.0, assets, tags, devices);
      // Post the Scene to the endpoint
      testLogger.println("Executing Query HTTP Request");
      ResponseEntity<Map> queryResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneUrl, scn, Map.class);
      // Read the response
      testLogger.println("Query Response Code:");
      testLogger.println(queryResponse.getStatusCode());
      assert (queryResponse.getStatusCode().is2xxSuccessful());
    } catch (Exception e) {
      e.printStackTrace(testLogger);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
