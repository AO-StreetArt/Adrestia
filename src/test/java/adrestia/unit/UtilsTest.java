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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
* Testing the Utility Provider.
*/
@RunWith(SpringRunner.class)
public class UtilsTest {

  // Test the Utility Provider
  @Test
  public void testSceneList() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testUtils.txt", "UTF-8");
    testLogger.println("Starting Test for Scene List");
    try {
      UtilityProvider utils = new UtilityProvider();
      Assert.assertTrue(utils.translateDvsError(100) == HttpStatus.OK);
      Assert.assertTrue(utils.translateDvsError(102) == HttpStatus.NO_CONTENT);
      Assert.assertTrue(utils.translateDvsError(110) == HttpStatus.BAD_REQUEST);
      Assert.assertTrue(utils.translateDvsError(122) == HttpStatus.NOT_ACCEPTABLE);
      Assert.assertTrue(utils.translateDvsError(101) == HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      Assert.assertTrue(false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }
}
