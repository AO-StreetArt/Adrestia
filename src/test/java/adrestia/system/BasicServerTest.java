
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

package adrestia.system;

import adrestia.Adrestia;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
* Basic Tests against the Adrestia Server.
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Adrestia.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=5889"})
public class BasicServerTest {
  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
  private int mgtPort;

  @Autowired
  private TestRestTemplate testTemplate;

  // Test the Actuator Health Check endpoint, which gets hit by Consul
  @Test
  public void testActuatorHealthCheck() throws Exception {
    ResponseEntity<Map> response = this.testTemplate.getForEntity(
        "http://localhost:" + this.mgtPort + "/info", Map.class);

    Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
  }
}
