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

import adrestia.ClymanConnector;
import adrestia.ObjectDao;
import java.io.PrintWriter;
import java.util.Properties;
import org.junit.Assert;
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
* Testing Object Data Access Object.
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ObjectDaoTest {

  @Autowired
  ObjectDao objData;

  private static final double tolerance = 0.001;

  /**
   * Test the Object Data Access.
   */
  @Test
  public void testObjectCrud() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testDao_obj.txt", "UTF-8");
    testLogger.println("Starting Test for Object Dao");
    try {
      // Create test
      double[] translation = {0.0, 0.0, 0.0};
      double[] rotationEuler = {0.0, 1.0, 0.0, 0.0};
      double[] scale = {1.0, 1.0, 1.0};
      String[] assets = {"TestAsset1", "TestAsset2"};
      ObjectDocument testDocument = new ObjectDocument("TestKey", "TestName",
          "TestType", "TestSubtype", "TestOwner", "TestScene",
          translation, rotationEuler, scale, assets, null);
      ObjectList crtResp = objData.create(testDocument);
      testLogger.println("Create Test Response: ");
      testLogger.println(crtResp);
      testLogger.println(crtResp.getErrorCode());
      testLogger.println(crtResp.getErrorMessage());
      Assert.assertTrue(crtResp.getErrorCode() == 100);
      String clymanKey = crtResp.getDocuments()[0].getKey();

      // Get test
      ObjectList getResp = objData.get(clymanKey);
      Assert.assertTrue(getResp.getErrorCode() == 100);
      Assert.assertTrue(getResp.getNumRecords() > 0);
      testLogger.println(getResp);
      Assert.assertTrue(getResp.getDocuments()[0].getType().equals("TestType"));
      Assert.assertTrue(getResp.getDocuments()[0].getOwner().equals("TestOwner"));

      // Update test
      testDocument.setKey(clymanKey);
      testDocument.setType("TestType2");
      testDocument.setOwner("TestOwner2");
      ObjectList updResp = objData.update(testDocument);
      Assert.assertTrue(updResp.getErrorCode() == 100);
      ObjectList getResp2 = objData.get(clymanKey);
      Assert.assertTrue(getResp2.getErrorCode() == 100);
      Assert.assertTrue(getResp2.getNumRecords() > 0);
      Assert.assertTrue(getResp2.getDocuments()[0].getType().equals("TestType2"));
      Assert.assertTrue(getResp2.getDocuments()[0].getOwner().equals("TestOwner2"));

      // Overwrite Test
      double[] ovrTranslation = {1.0, 1.0, 1.0};
      double[] ovrRotationEuler = {0.0, 1.0, 0.0, 0.0};
      double[] ovrScale = {1.0, 1.0, 1.0};
      String[] ovrAssets = {"TestAsset1", "TestAsset2"};
      ObjectDocument overwriteTestDoc = new ObjectDocument(clymanKey, "TestName",
          "TestType", "TestSubtype", "TestOwner", "TestScene",
          ovrTranslation, ovrRotationEuler, ovrScale, ovrAssets, null);
      ObjectList ovrResp = objData.overwrite(overwriteTestDoc);
      testLogger.println("Overwrite Test Response: ");
      testLogger.println(ovrResp.getErrorCode());
      testLogger.println(ovrResp.getErrorMessage());
      Assert.assertTrue(ovrResp.getErrorCode() == 100);
      ObjectList ovrCheckResp = objData.get(clymanKey);
      Assert.assertTrue(ovrCheckResp.getErrorCode() == 100);
      Assert.assertTrue(ovrCheckResp.getNumRecords() > 0);
      Assert.assertTrue(ovrCheckResp.getDocuments()[0].getTransform()[0] - 1.0 < 0.001);
      Assert.assertTrue(ovrCheckResp.getDocuments()[0].getTransform()[3] - 1.0 < 0.001);

      // Lock Test
      ObjectList lockResp1 = objData.lock(clymanKey, "ud1");
      Assert.assertTrue(lockResp1.getErrorCode() == 100);
      ObjectList lockResp2 = objData.lock(clymanKey, "ud2");
      Assert.assertTrue(lockResp2.getErrorCode() != 100);
      ObjectList lockResp3 = objData.unlock(clymanKey, "ud1");
      Assert.assertTrue(lockResp3.getErrorCode() == 100);
      ObjectList lockResp4 = objData.lock(clymanKey, "ud2");
      Assert.assertTrue(lockResp4.getErrorCode() == 100);
      ObjectList lockResp5 = objData.unlock(clymanKey, "ud2");
      Assert.assertTrue(lockResp5.getErrorCode() == 100);

      // Delete test
      ObjectList delResp = objData.destroy(clymanKey);
      Assert.assertTrue(delResp.getErrorCode() == 100);
      ObjectList getResp3 = objData.get(clymanKey);
      Assert.assertTrue(getResp3.getErrorCode() == 102);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      Assert.assertTrue(false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

}
