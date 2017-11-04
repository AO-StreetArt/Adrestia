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
import org.json.JSONObject;
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
public class ZmqConnectionTest {

  @Autowired
  ZmqConnection socket;

  /**
   * Test the ZMQ Connection to Crazy Ivan
   */
  @Test
  public void testIvanConnection() throws Exception {
    // Open up a file that we can write some test results to
    // Shouldn't be relied on for automated testing but good for debugging
    PrintWriter testLogger = new PrintWriter("logs/testZmq.txt", "UTF-8");
    testLogger.println("Starting Test for ZMQ Connection");
    try {
      // Build a ping message
      String pingString = "{\"msg_type\": 555}";
      // Send the ping message
      String response = socket.send(pingString, 10000, 5, "Ivan");
      testLogger.println("Ping Response: " + response);
      // Parse the response and validate the success code
      JSONObject responseObj = new JSONObject(response);
      assert (responseObj.getInt("err_code") == 100);
    } catch (Exception e) {
      e.printStackTrace(testLogger);
      assert (false);
    } finally  {
      // Close the output text file
      testLogger.close();
    }
  }

  // TO-DO: Test CLyman Connection

  // TO-DO: Multi-threaded connection test
}
