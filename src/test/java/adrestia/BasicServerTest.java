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
* Basic Tests against the Adrestia Server, mostly covering actuator endpoints
* API-Specific Tests will go in other test files
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Adrestia.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=5889"})
public class BasicServerTest {
  @LocalServerPort
  private int port;

  @Value("${local.management.port}")
	private int mgt_port;

  @Autowired
  private TestRestTemplate test_template;

  @Test
  public void testActuatorHealthCheck() throws Exception {
    ResponseEntity<Map> response = this.test_template.getForEntity(
                      "http://localhost:" + this.mgt_port + "/info", Map.class);

    System.out.println("HTTP Success Code: ");
    System.out.println(response.getStatusCode());
    assert (response.getStatusCode().is2xxSuccessful());
  }
}
