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

import adrestia.CrazyIvanConnector;
import adrestia.SceneDao;
import java.io.PrintWriter;
import java.util.Properties;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
* Testing Scene Data Access Object.
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class SceneDaoTest {

  @Autowired
  SceneDao scnData;

  private static final double tolerance = 0.001;

  // Convenience method to build a test transform element
  private Transform buildTransform() {
    double[] translation = {1.0, 2.0, 3.0};
    double[] rotation = {45.0, 1.0, 0.0, 0.0};
    return new Transform(translation, rotation);
  }

  // Convenience method to validate the contents of a test transform element
  private void assertTransformElements(Transform trans) {
    assert (trans.getTranslation()[0] - 1.0 < tolerance);
    assert (trans.getTranslation()[1] - 2.0 < tolerance);
    assert (trans.getTranslation()[2] - 3.0 < tolerance);
    assert (trans.getRotation()[0] - 45.0 < tolerance);
    assert (trans.getRotation()[1] - 1.0 < tolerance);
    assert (trans.getRotation()[2] - 0.0 < tolerance);
    assert (trans.getRotation()[3] - 0.0 < tolerance);
  }

  // Convenience method to build a test User Device
  private UserDevice buildDevice() {
    Transform trans = buildTransform();
    return new UserDevice("MyTestKey", trans);
  }

  // Convenience method to validate the contents of a test user device element
  private void assertDeviceElements(UserDevice dev) {
    assertTransformElements(dev.getTransform());
    assert (dev.getKey().equals("MyTestKey"));
  }

  // Convenience method to build a test scene
  private Scene buildScene() {
    UserDevice dev = buildDevice();
    UserDevice[] devList = {dev};
    String[] tags = {"ABC", "123"};
    String[] assets = {"FirstAsset", "SecondAsset"};
    return new Scene("aNewKey", "aNewName", "MyRegion", 100.0, 120.0, 0.0, assets, tags, devList);
  }

  // Convenience method to validate the contents of a test scene
  private void assertSceneElements(Scene scn) {
    assert (scn.getKey().equals("aNewKey"));
    assert (scn.getName().equals("aNewName"));
    assert (scn.getRegion().equals("MyRegion"));
    assert (scn.getLatitude() - 100.0 < tolerance);
    assert (scn.getLongitude() - 120.0 < tolerance);
    assert (scn.getDistance() < tolerance);
    assert (scn.getAssets().length == 2);
    assert (scn.getAssets()[0].equals("FirstAsset"));
    assert (scn.getAssets()[1].equals("SecondAsset"));
    assert (scn.getTags().length == 2);
    assert (scn.getTags()[0].equals("ABC"));
    assert (scn.getTags()[1].equals("123"));
    assertDeviceElements(scn.getDevices()[0]);
  }

  // Convenience method to build a test scene list
  private SceneList buildSceneList() {
    // Full Constructor Test
    Scene scn = buildScene();
    Scene[] scnArray = {scn};
    return new SceneList(0, scnArray);
  }

  // Convenience method to validate the contents of a scene list
  private void assertSceneListElements(SceneList scnList) {
    assert (scnList.getMsgType() == 0);
    assertSceneElements(scnList.getSceneList()[0]);
  }

  /**
   * Test the Scene Data Access Object - Creation Method.
   */
  @Test
  public void testSceneCreation() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneCrt.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");

    try {
      // Scene Creation Test
      Scene scn = buildScene();
      assertSceneElements(scn);
      SceneList resp = scnData.create(scn);
      testLogger.println("Test Response: ");
      testLogger.println(resp.getErrorCode());
      testLogger.println(resp.getErrorMessage());
      assert (resp.getErrorCode() == 100);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  /**
   * Test the Scene Data Access Object - Get Method.
   */
  @Test
  public void testSceneGet() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneGet.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");

    try {
      Scene scn3 = buildScene();
      scn3.setKey("ThirdKey");
      scn3.setName("ThirdName");
      scn3.setLatitude(106.3);
      scn3.setLongitude(107.5);
      SceneList resp3 = scnData.create(scn3);
      assert (resp3.getErrorCode() == 100);
      // Retrieve a scene
      SceneList resp = scnData.get("ThirdName");
      testLogger.println("Test Response: ");
      testLogger.println(resp.getErrorCode());
      testLogger.println(resp.getErrorMessage());
      // Validate that the correct contents are returned
      assert (resp.getErrorCode() == 100);
      Scene scn = resp.getSceneList()[0];
      assert (scn.getKey().equals("ThirdKey"));
      assert (scn.getName().equals("ThirdName"));
      assert (scn.getRegion().equals("MyRegion"));
      assert (scn.getLatitude() - 106.3 < tolerance);
      assert (scn.getLongitude() - 107.5 < tolerance);
      assert (scn.getDistance() < tolerance);
      assert (scn.getAssets().length == 2);
      assert (scn.getAssets()[0].equals("FirstAsset"));
      assert (scn.getAssets()[1].equals("SecondAsset"));
      assert (scn.getTags().length == 2);
      assert (scn.getTags()[0].equals("ABC"));
      assert (scn.getTags()[1].equals("123"));
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  /**
   * Test the Scene Data Access Object - Update Method.
   */
  @Test
  public void testSceneUpdate() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneUpd.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");

    try {
      Scene originalScn = buildScene();
      originalScn.setKey("SceneDaoUpdateKey");
      originalScn.setName("SceneDaoUpdateName");
      originalScn.setLatitude(102.3);
      originalScn.setLongitude(103.5);
      SceneList resp1 = scnData.create(originalScn);
      assert (resp1.getErrorCode() == 100);
      // Create an updated scene
      Scene scn1 = buildScene();
      scn1.setKey("SceneDaoUpdateKey");
      scn1.setName("SceneDaoUpdateName");
      scn1.setLatitude(109.9);
      scn1.setLongitude(113.8);
      // Not expected to be persisted
      scn1.setDistance(25.0);
      // Update the existing Scene
      SceneList resp = scnData.update(scn1);
      testLogger.println("Test Response: ");
      testLogger.println(resp.getErrorCode());
      testLogger.println(resp.getErrorMessage());
      // Validate that the update is persisted
      assert (resp.getErrorCode() == 100);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  /**
   * Test the Scene Data Access Object - Delete Method.
   */
  @Test
  public void testSceneDestroy() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneDel.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");
    try {
      // Create the scene
      Scene scn4 = buildScene();
      scn4.setKey("FourthKey");
      scn4.setName("FourthName");
      SceneList resp4 = scnData.create(scn4);
      assert (resp4.getErrorCode() == 100);
      // Delete the scene
      SceneList resp = scnData.destroy("FourthKey");
      testLogger.println("Test Response: ");
      testLogger.println(resp.getErrorCode());
      testLogger.println(resp.getErrorMessage());
      // Validate that the scene was deleted
      assert (resp.getErrorCode() == 100);
      SceneList getResp = scnData.get("FourthName");
      testLogger.println("Get Response: ");
      testLogger.println(getResp.getErrorCode());
      testLogger.println(getResp.getErrorMessage());
      assert (getResp.getErrorCode() == 102);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  /**
   * Test the Scene Data Access Object - Registration Method.
   */
  @Test
  public void testSceneRegistration() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneReg.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");
    try {
      // Create the scene & Register the device to it
      SceneList resp = scnData.register("RegDaoTestSceneKey", "RegDaoTestDeviceKey", null);
      testLogger.println("Test Response: ");
      testLogger.println(resp.getErrorCode());
      testLogger.println(resp.getErrorMessage());
      // Validate that the device was registered
      assert (resp.getErrorCode() == 100);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  /**
   * Test the Scene Data Access Object - Synchronization Method.
   */
  @Test
  public void testSceneSynchronization() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneSync.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");
    try {
      // Create the Scene
      Scene baseScn = buildScene();
      baseScn.setName("RegDaoTestSceneName2");
      SceneList crtResp = scnData.create(baseScn);
      assert (crtResp.getErrorCode() == 100);
      // Register the device to it
      SceneList resp = scnData.register("RegDaoTestSceneName2", "RegDaoTestDeviceKey2", null);
      // Validate that the device was registered
      assert (resp.getErrorCode() == 100);
      // Create a transform to update the server with
      double[] translation = {1.0, 2.0, 3.0};
      double[] rotation = {30.0, 15.0, 5.0};
      Transform transform = new Transform(translation, rotation);
      // Synchronize the device-scene transformation
      SceneList syncResp =
          scnData.synchronize("RegDaoTestSceneName2", "RegDaoTestDeviceKey2", transform);
      testLogger.println("Test Response: ");
      testLogger.println(syncResp.getErrorCode());
      testLogger.println(syncResp.getErrorMessage());
      // Validate that the device was synchronized
      assert (syncResp.getErrorCode() == 100);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  /**
   * Test the Scene Data Access Object - Deregistration Method.
   */
  @Test
  public void testSceneDeregistration() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_sceneDereg.txt", "UTF-8");
    testLogger.println("Starting Test for Scene Dao");
    try {
      // Create the scene & Register the device to it
      SceneList resp = scnData.register("RegDaoTestSceneKey3", "RegDaoTestDeviceKey3", null);
      // Validate that the device was registered
      assert (resp.getErrorCode() == 100);
      // De-register the device from the scene
      SceneList deregResp = scnData.deregister("RegDaoTestSceneKey3", "RegDaoTestDeviceKey3");
      testLogger.println("Test Response: ");
      testLogger.println(deregResp.getErrorCode());
      testLogger.println(deregResp.getErrorMessage());
      // Validate that the device was registered
      assert (deregResp.getErrorCode() == 100);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
