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
* Testing Object3 Data Model Components.
*/
@RunWith(SpringRunner.class)
public class ObjTest {

  private static final double tolerance = 0.001;

  // Test the Object Document Class
  @Test
  public void testObjectDocument() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testModel_obj.txt", "UTF-8");
    testLogger.println("Starting Test for Object Document");
    try {
      // Full Constructor Test
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotationEuler = {0.0, 0.0, 0.0, 0.0};
      double[] scale = {0.0, 0.0, 0.0};
      String[] assets = {"TestAsset1", "TestAsset2"};
      ObjectDocument testDocument = new ObjectDocument("TestKey", "TestName",
          "TestType", "TestSubtype", "TestOwner",
          translation, rotationEuler, scale, assets);
      // Test the get methods
      assert (testDocument.getKey().equals("TestKey"));
      assert (testDocument.getName().equals("TestName"));
      assert (testDocument.getType().equals("TestType"));
      assert (testDocument.getSubtype().equals("TestSubtype"));
      assert (testDocument.getOwner().equals("TestOwner"));
      assert (testDocument.getTranslation()[0] - 0.0 < tolerance);
      assert (testDocument.getRotationEuler()[0] - 0.0 < tolerance);
      assert (testDocument.getScale()[0] - 0.0 < tolerance);
      assert (testDocument.getAssets()[0].equals("TestAsset1"));
      // Test the set methods
      testDocument.setKey("TestKey2");
      testDocument.setName("TestName2");
      testDocument.setType("TestType2");
      testDocument.setSubtype("TestSubtype2");
      testDocument.setOwner("TestOwner2");
      double[] translation2 = {1.0, 1.0, 1.0};
      double[] rotationEuler2 = {1.0, 0.0, 0.0, 1.0};
      double[] scale2 = {1.0, 1.0, 1.0};
      String[] assets2 = {"TestAsset3", "TestAsset4"};
      testDocument.setTranslation(translation2);
      testDocument.setRotationEuler(rotationEuler2);
      testDocument.setScale(scale2);
      testDocument.setAssets(assets2);
      assert (testDocument.getKey().equals("TestKey2"));
      assert (testDocument.getName().equals("TestName2"));
      assert (testDocument.getType().equals("TestType2"));
      assert (testDocument.getSubtype().equals("TestSubtype2"));
      assert (testDocument.getOwner().equals("TestOwner2"));
      assert (testDocument.getTranslation()[0] - 1.0 < tolerance);
      assert (testDocument.getRotationEuler()[0] - 1.0 < tolerance);
      assert (testDocument.getScale()[0] - 1.0 < tolerance);
      assert (testDocument.getAssets()[0].equals("TestAsset3"));
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // Test the Object List Class
  @Test
  public void testObjectList() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testModel_objList.txt", "UTF-8");
    testLogger.println("Starting Test for Object List");
    try {
      // Full Constructor Test
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotationEuler = {0.0, 0.0, 0.0, 0.0};
      double[] scale = {0.0, 0.0, 0.0};
      String[] assets = {"TestAsset1", "TestAsset2"};
      ObjectDocument testDocument = new ObjectDocument("TestKey", "TestName",
          "TestType", "TestSubtype", "TestOwner",
          translation, rotationEuler, scale, assets);

      ObjectDocument[] docList = {testDocument};
      ObjectList objList = new ObjectList(1, 1, docList, 100, "ErrMsg", "TransactionID");
      assert (objList.getMsgType() == 1);
      assert (objList.getNumRecords() == 1);
      assert (objList.getErrorCode() == 100);
      assert (objList.getErrorMessage().equals("ErrMsg"));
      assert (objList.getTransactionId().equals("TransactionID"));
      assert (objList.getDocuments()[0].getKey().equals("TestKey"));
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
