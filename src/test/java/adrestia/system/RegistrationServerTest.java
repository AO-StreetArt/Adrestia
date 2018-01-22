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
import java.util.HashMap;
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
public class RegistrationServerTest {
  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgtPort;

  @Autowired
  private TestRestTemplate testTemplate;

  // Convenience method to build a test scene
  private Scene buildTestScene(String sceneKey, String sceneName) {
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
    return new Scene(
        sceneKey, sceneName, "C", 1.0, 2.0, 3.0, assets, tags, devices);
  }

  // Test Registration Function for devices
  @Test
  public void testRegistrationApi() throws Exception {
    String testSceneBaseUrl = "/v1/scene/myscene";
    String testSceneUrl = testSceneBaseUrl + "/registration";
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testRegistrationApi.txt", "UTF-8");
    try {
      // Run Registration Test
      // Build a base scene
      testLogger.println("Building Base data");
      ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneBaseUrl, buildTestScene("firstRegTest", "myscene"), Map.class);
      testLogger.println(createResponse.getStatusCode());
      // Read the response
      assert (createResponse.getStatusCode().is2xxSuccessful());
      testLogger.println("Starting Scene Registration Tests");
      // Build a map of the URL Variables
      HashMap urlVariables = new HashMap();
      urlVariables.put("device_id", "12345");
      urlVariables.put("device_host", "testhost");
      urlVariables.put("device_port", 5555);
      // Build a new Transform
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotation = {0.0, 0.0, 0.0, 0.0};
      Transform transform = new Transform(translation, rotation);
      // Put the registration to the endpoint
      testLogger.println("Executing Query HTTP Request");
      this.testTemplate.put(
          "http://localhost:" + this.port + testSceneUrl, transform, urlVariables);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test Registration Function for devices
  @Test
  public void testSyncApi() throws Exception {
    String testSceneBaseUrl = "/v1/scene/synctest";
    String testSceneUrl = testSceneBaseUrl + "/registration";
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testSyncApi.txt", "UTF-8");
    try {
      // Run Sync Tests
      // Build a base scene
      testLogger.println("Building Base data");
      ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneBaseUrl, buildTestScene("secondRegTest", "synctest"), Map.class);
      testLogger.println(createResponse.getStatusCode());
      // Read the response
      assert (createResponse.getStatusCode().is2xxSuccessful());
      // Start by building some base data
      testLogger.println("Starting Scene Sync Tests");
      // Build a new Transform
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotation = {0.0, 0.0, 0.0, 0.0};
      Transform transform = new Transform(translation, rotation);
      // Put the registration to the endpoint
      testLogger.println("Executing Query HTTP Request");
      this.testTemplate.put(
          "http://localhost:" + this.port + testSceneUrl, transform);

      // Now, execute a sync against that data
      // Build a new Transform
      double[] translation2 = {5.0, 6.0, 7.0};
      double[] rotation2 = {2.0, 0.99, 0.1, 0.1};
      Transform transform2 = new Transform(translation2, rotation2);
      // Post the registration to the endpoint
      testLogger.println("Executing Query HTTP Request");
      ResponseEntity<Map> queryResponse2 = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneUrl, transform2, Map.class);
      // Read the response
      testLogger.println("Sync Response Code:");
      testLogger.println(queryResponse2.getStatusCode());
      assert (queryResponse2.getStatusCode().is2xxSuccessful());
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test Registration Function for devices
  @Test
  public void testDegistrationApi() throws Exception {
    String testSceneBaseUrl = "/v1/scene/deregtest";
    String testSceneUrl = testSceneBaseUrl + "/registration";
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDeregistrationApi.txt", "UTF-8");
    try {
      // Run Deregistration Tests
      // Build a base scene
      testLogger.println("Building Base data");
      ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testSceneBaseUrl, buildTestScene("secondRegTest", "synctest"), Map.class);
      testLogger.println(createResponse.getStatusCode());
      // Read the response
      assert (createResponse.getStatusCode().is2xxSuccessful());
      // Build some data
      testLogger.println("Starting Scene Deregistration Tests");
      // Build a new Transform
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotation = {0.0, 0.0, 0.0};
      Transform transform = new Transform(translation, rotation);
      // Post the registration to the endpoint
      testLogger.println("Executing Query HTTP Request");
      this.testTemplate.put(
          "http://localhost:" + this.port + testSceneUrl, transform);

      // Now Delete the registration
      this.testTemplate.delete(
          "http://localhost:" + this.port + testSceneUrl, Map.class);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
