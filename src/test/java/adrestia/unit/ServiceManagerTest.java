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

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.util.ReflectionTestUtils;

/**
* Testing the Service Manager Component (communicates with Service Discovery).
*/
@RunWith(MockitoJUnitRunner.class)
public class ServiceManagerTest {

  @Mock
  private DiscoveryClient consulClient;

  @Mock
  private UtilityProviderInterface utils;

  @InjectMocks
  private ServiceManagerInterface serviceManager = new ServiceManager();

  /**
  * Test startup
  */
  @Before
  public void setUp() {
    // Set the configuration values in the service manager
    ReflectionTestUtils.setField(serviceManager, "redlistDuration", 10);
    ReflectionTestUtils.setField(serviceManager, "greylistDuration", 30);
    ReflectionTestUtils.setField(serviceManager, "blacklistDuration", 300);
  }

  // Basic service discovery test
  @Test
  public void testBasicDiscovery() {

    // Return 0 for all random integers
    when(utils.getRandomInt(anyInt())).thenReturn(0);

    // Return a default service instance from the consul client
    when(consulClient.getInstances(anyString())).thenReturn(
        new ArrayList<ServiceInstance>(
            Arrays.asList(
                new DefaultServiceInstance("TestId", "TestHost", 5555, false)
            )
        )
    );

    // Find an instance of Crazy Ivan from the service manager
    ServiceInstance ivanInstance = serviceManager.findCrazyIvan();
    assert (ivanInstance.getHost().equals("TestHost"));
    assert (ivanInstance.getPort() == 5555);

    // Find an instance of CLyman from the service manager
    ServiceInstance clymanInstance = serviceManager.findClyman();
    assert (clymanInstance.getHost().equals("TestHost"));
    assert (clymanInstance.getPort() == 5555);
  }

  // Failure Reporting Test
  @Test
  public void testFailedServices() throws InterruptedException {

    // Return 0 for all random integers
    when(utils.getRandomInt(anyInt())).thenReturn(0);

    // Return a default service instance from the consul client
    when(consulClient.getInstances(anyString())).thenReturn(
        new ArrayList<ServiceInstance>(
            Arrays.asList(
                new DefaultServiceInstance("TestId1", "TestHost1", 5555, false),
                new DefaultServiceInstance("TestId2", "TestHost2", 5556, false)
            )
        )
    );

    // Pull Down and validate the first instance
    // Find an instance of Crazy Ivan from the service manager
    ServiceInstance ivanInstance = serviceManager.findCrazyIvan();
    assert (ivanInstance.getHost().equals("TestHost1"));
    assert (ivanInstance.getPort() == 5555);

    // Report a failure on the instance
    serviceManager.reportFailure(ivanInstance);

    // Pull down and validate the second instance
    ServiceInstance secondInstance = serviceManager.findCrazyIvan();
    assert (secondInstance != null);
    assert (secondInstance.getHost().equals("TestHost2"));
    assert (secondInstance.getPort() == 5556);

    // Report a failure on the instance
    serviceManager.reportFailure(secondInstance);

    // Sleep until the first instance is off the red list
    Thread.sleep(11000);

    // Pull Down and validate the first instance
    ServiceInstance firstInstance = serviceManager.findCrazyIvan();
    assert (firstInstance.getHost().equals("TestHost1"));
    assert (firstInstance.getPort() == 5555);

    // Report a failure on the instance
    serviceManager.reportFailure(firstInstance);

    // Pull down and validate the second instance
    ServiceInstance finalInstance = serviceManager.findCrazyIvan();
    assert (finalInstance.getHost().equals("TestHost2"));
    assert (finalInstance.getPort() == 5556);

    // Report a failure on the instance
    serviceManager.reportFailure(finalInstance);

    ServiceInstance noInstance = serviceManager.findCrazyIvan();
    assert (noInstance == null);
  }
}
