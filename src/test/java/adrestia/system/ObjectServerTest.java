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
* Basic Object Tests against the Adrestia Server.
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Adrestia.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=5889"})
public class ObjectServerTest {
  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgtPort;

  @Autowired
  private TestRestTemplate testTemplate;

  // Test basic CRUD Functions for objects
  @Test
  public void testObjectCrudApi() throws Exception {
    String testSceneName = "AeselTestScene11";
    String testObjectName = "AeselTestObject11";
    String testObjectUrl = "/v1/scene/" + testSceneName + "/object/" + testObjectName;
    double tolerance = 0.00001;
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testObjectCrudApi.txt", "UTF-8");
    try {
      testLogger.println("Starting Test for Object CRUD API");
      // Create Test
      // Build a new Object
      testLogger.println("Create Test");
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotationEuler = {0.0, 0.0, 0.0, 0.0};
      double[] scale = {1.0, 1.0, 1.0};
      String[] assets = {"TestAsset1", "TestAsset2"};
      ObjectDocument obj = new ObjectDocument("TestKey", "AeselTestObject11",
          "TestType", "TestSubtype", "TestOwner", "AeselTestScene11",
          translation, rotationEuler, scale, assets, null);

      // Post the Object to the endpoint
      ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testObjectUrl, obj, Map.class);
      testLogger.println(createResponse.getStatusCode());
      // Read the response
      assert (createResponse.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map crtRespBody = createResponse.getBody();
      testLogger.println("Create Response: " + crtRespBody.toString());
      testLogger.println("Key: " + crtRespBody.get("key"));
      assert (!(crtRespBody.get("key").toString().isEmpty()));

      // Get Test
      testLogger.println("Get Test");
      // Issue a get request for the object just created
      ResponseEntity<Map> getResponse = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testObjectUrl, Map.class);
      // Read the response
      assert (getResponse.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map getRespBody = getResponse.getBody();
      testLogger.println("Get Response: " + getRespBody.toString());
      assert (getRespBody.get("type").equals("TestType"));

      // Update Test
      testLogger.println("Update Test");
      double[] translation2 = {1.0, 1.0, 1.0};
      double[] rotationEuler2 = {1.0, 1.0, 1.0, 45.0};
      double[] scale2 = {2.0, 2.0, 2.0};
      String[] assets2 = {"TestAsset3", "TestAsset4"};
      ObjectDocument obj2 = new ObjectDocument("TestKey", "AeselTestObject11",
          "TestType2", "TestSubtype2", "TestOwner2", "AeselTestScene11",
          translation2, rotationEuler2, scale2, assets2, null);
      // Post the Scene to the endpoint
      ResponseEntity<Map> updateResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testObjectUrl, obj2, Map.class);
      // Read the response
      assert (updateResponse.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map updRespBody = updateResponse.getBody();
      testLogger.println("Update Response: " + updRespBody.toString());

      // Run a secondary get test to validate the update
      testLogger.println("Get Test");
      // Issue a get request for the scene just updated
      ResponseEntity<Map> getResponse2 = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testObjectUrl, Map.class);
      // Read the response
      assert (getResponse2.getStatusCode().is2xxSuccessful());
      // Validate the response
      Map getRespBody2 = getResponse2.getBody();
      testLogger.println("Get Response: " + getRespBody2.toString());
      assert (getRespBody2.get("type").equals("TestType2"));

      // Overwrite Test
      testLogger.println("Create Test");
      double[] ovrTranslation = {1.0, 1.0, 1.0};
      double[] ovrRotationEuler = {0.0, 0.0, 0.0, 0.0};
      double[] ovrScale = {1.0, 1.0, 1.0};
      String[] ovrAssets = {"TestAsset1", "TestAsset2"};
      String clymanKey = crtRespBody.get("key").toString();
      ObjectDocument ovrObj = new ObjectDocument(clymanKey, "AeselTestObject11",
          "TestType", "TestSubtype", "TestOwner", "AeselTestScene11",
          ovrTranslation, ovrRotationEuler, ovrScale, ovrAssets, null);
      ResponseEntity<Map> ovrResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + "/v1/object/" + clymanKey, ovrObj, Map.class);
      testLogger.println(ovrResponse.getStatusCode());
      // Read the response
      assert (ovrResponse.getStatusCode().is2xxSuccessful());

      // Delete Test
      testLogger.println("Delete Test");
      // Delete the scene
      this.testTemplate.delete(
          "http://localhost:" + this.port + testObjectUrl, Map.class);

      // Run a tertiary get test to validate the update
      testLogger.println("Get Test");
      // Issue a get request for the scene just updated
      ResponseEntity<Map> getResponse3 = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testObjectUrl, Map.class);
      // Read the response
      testLogger.println("Delete Test Response Code");
      testLogger.println(getResponse3.getStatusCode());
      testLogger.println("Get Response: " + getResponse3.toString());
      assert (getResponse3.getStatusCode().is4xxClientError());
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  private void populateQueryData() {
    final String testObjectUrl = "/v1/scene/MyFirstQueryScene/object/obj1";
    final String testObjectUrl2 = "/v1/scene/MyFirstQueryScene/object/obj2";
    double[] translation = {0.0, 0.0, 0.0};
    double[] rotationEuler = {0.0, 0.0, 0.0, 0.0};
    double[] scale = {1.0, 1.0, 1.0};
    String[] assets = {"TestAsset1", "TestAsset2"};
    ObjectDocument obj = new ObjectDocument("TestKey", "obj1",
        "TestType", "TestSubtype", "TestOwner", "MyFirstQueryScene",
        translation, rotationEuler, scale, assets, null);
    // Post the Scene to the endpoint
    ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
        "http://localhost:" + this.port + testObjectUrl, obj, Map.class);
    // Read the response
    assert (createResponse.getStatusCode().is2xxSuccessful());
    ObjectDocument obj2 = new ObjectDocument("TestKey", "obj2",
        "TestType2", "TestSubtype2", "TestOwner2", "MyFirstQueryScene",
        translation, rotationEuler, scale, assets, null);
    // Post the Scene to the endpoint
    ResponseEntity<Map> createResponse2 = this.testTemplate.postForEntity(
        "http://localhost:" + this.port + testObjectUrl2, obj2, Map.class);
    // Read the response
    assert (createResponse2.getStatusCode().is2xxSuccessful());
  }

  // Test Query Functions for objects
  @Test
  public void testObjectQueryApi() throws Exception {
    final String testObjectUrl = "/v1/scene/MyFirstQueryScene/object";
    double tolerance = 0.00001;
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testObjectQueryApi.txt", "UTF-8");
    try {
      testLogger.println("Populating Data for Query Tests");
      populateQueryData();
      // Run Query Tests
      testLogger.println("Starting Object Query Tests");
      HashMap urlVariables = new HashMap();
      urlVariables.put("type", "TestType2");
      // Post the Scene to the endpoint
      testLogger.println("Executing Query HTTP Request");
      ResponseEntity<Map> queryResponse = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testObjectUrl, Map.class, urlVariables);
      // Read the response
      testLogger.println("Query Response Code:");
      testLogger.println(queryResponse.getStatusCode());
      assert (queryResponse.getStatusCode().is2xxSuccessful());
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test Lock Functions for objects
  // TO-DO: Change out POST for correct functions and replace post data with request params
  @Test
  public void testObjectLockApi() throws Exception {
    final String testObjectUrl = "/v1/scene/MyLockScene/object/obj1";
    final String testObjectLockUrl = testObjectUrl + "/lock";
    double tolerance = 0.00001;
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testObjectLockApi.txt", "UTF-8");
    try {
      testLogger.println("Populating Data for Lock Tests");
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotationEuler = {0.0, 0.0, 0.0, 0.0};
      double[] scale = {1.0, 1.0, 1.0};
      String[] assets = {"TestAsset1", "TestAsset2"};
      ObjectDocument obj = new ObjectDocument("TestKey", "obj1",
          "TestType", "TestSubtype", "TestOwner", "MyLockScene",
          translation, rotationEuler, scale, assets, null);
      // Post the object to the endpoint
      ResponseEntity<Map> createResponse = this.testTemplate.postForEntity(
          "http://localhost:" + this.port + testObjectUrl, obj, Map.class);
      // Read the response
      assert (createResponse.getStatusCode().is2xxSuccessful());
      // Run Lock Tests
      testLogger.println("Starting Object Lock Tests");
      // Get the lock
      testLogger.println("Executing Lock Request");
      HashMap urlVariables = new HashMap();
      urlVariables.put("owner", "123");
      ResponseEntity<Map> lockResponse = this.testTemplate.getForEntity(
          "http://localhost:" + this.port + testObjectLockUrl, Map.class, urlVariables);
      // Read the response
      testLogger.println("Lock Response Code:");
      testLogger.println(lockResponse.getStatusCode());
      assert (lockResponse.getStatusCode().is2xxSuccessful());
      // Run unlock test
      testLogger.println("Executing Unlock Request");
      HashMap urlVariables2 = new HashMap();
      urlVariables2.put("owner", "123");
      this.testTemplate.delete(
          "http://localhost:" + this.port + testObjectLockUrl, Map.class, urlVariables2);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
