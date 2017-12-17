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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
* Testing Scene Data Model Components.
*/
@RunWith(SpringRunner.class)
public class SceneTest {

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
    return new UserDevice("MyTestKey", "TestHost", 5555, trans);
  }

  // Convenience method to validate the contents of a test user device element
  private void assertDeviceElements(UserDevice dev) {
    assertTransformElements(dev.getTransform());
    assert (dev.getKey().equals("MyTestKey"));
    assert (dev.getHost().equals("TestHost"));
    assert (dev.getPort() == 5555);
  }

  // Convenience method to build a test scene
  private Scene buildScene() {
    UserDevice dev = buildDevice();
    UserDevice[] devList = {dev};
    String[] tags = {"ABC", "123"};
    String[] assets = {"FirstAsset", "SecondAsset"};
    return new Scene("MyKey", "MyName", "MyRegion", 100.0, 120.0, 0.0, assets, tags, devList);
  }

  // Convenience method to validate the contents of a test scene
  private void assertSceneElements(Scene scn) {
    assert (scn.getKey().equals("MyKey"));
    assert (scn.getName().equals("MyName"));
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

  // Test the Transform component of the Scene List
  @Test
  public void testTransform() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testModel_transform.txt", "UTF-8");
    testLogger.println("Starting Test for Transform");
    try {
      // Full Constructor Test
      Transform trans = buildTransform();

      assertTransformElements(trans);

      // Default Constructor Test
      double[] defaultTranslation = {1.0, 2.0, 3.0};
      double[] defaultRotation = {45.0, 1.0, 0.0, 0.0};
      Transform defaultTrans = new Transform();
      defaultTrans.setTranslation(defaultTranslation);
      defaultTrans.setRotation(defaultRotation);

      assertTransformElements(defaultTrans);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test the User Device component of the Scene List
  @Test
  public void testUserDevice() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testModel_device.txt", "UTF-8");
    testLogger.println("Starting Test for User Device");
    try {
      // Full Constructor Test
      UserDevice dev = buildDevice();

      assertDeviceElements(dev);

      // Default Constructor Test
      Transform defaultTrans = buildTransform();
      UserDevice defaultDev = new UserDevice();
      defaultDev.setKey("MyTestKey");
      defaultDev.setHost("TestHost");
      defaultDev.setPort(5555);
      defaultDev.setTransform(defaultTrans);

      assertDeviceElements(defaultDev);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test the Scene component of the Scene List
  @Test
  public void testScene() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testModel_scene.txt", "UTF-8");
    testLogger.println("Starting Test for Scene");
    try {
      // Full Constructor Test
      Scene scn = buildScene();

      assertSceneElements(scn);

      // Default Constructor Test
      UserDevice dev = buildDevice();
      UserDevice[] devList = {dev};
      String[] tags = {"ABC", "123"};
      String[] assets = {"FirstAsset", "SecondAsset"};
      Scene defaultScn = new Scene();
      defaultScn.setKey("MyKey");
      defaultScn.setName("MyName");
      defaultScn.setRegion("MyRegion");
      defaultScn.setLatitude(100.0);
      defaultScn.setLongitude(120.0);
      defaultScn.setTags(tags);
      defaultScn.setAssets(assets);
      defaultScn.setDevices(devList);

      assertSceneElements(defaultScn);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test the Scene List Data Structure
  @Test
  public void testSceneList() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testModel_sceneList.txt", "UTF-8");
    testLogger.println("Starting Test for Scene List");
    try {
      // Full Constructor Test
      SceneList scnList = buildSceneList();
      assertSceneListElements(scnList);

      // Default Constructor Test
      Scene scn = buildScene();
      Scene[] scnArray = {scn};
      SceneList defaultScnList = new SceneList();
      defaultScnList.setMsgType(0);
      defaultScnList.setSceneList(scnArray);
      assertSceneListElements(defaultScnList);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
