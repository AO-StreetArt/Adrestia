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

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

/**
* Central Application.
* EnableDiscoveryClient - Turns on automated registration with Consul Agent
* on startup.
*/
@EnableDiscoveryClient
@Configuration
@SpringBootApplication
public class Adrestia extends AbstractMongoConfiguration {

  // Hostname of Mongo Connection
  @Value("${server.mongo.host}")
  private String mongoHost;

  // Hostname of Mongo Port
  @Value("${server.mongo.port}")
  private int mongoPort;

  @Bean
  public GridFsTemplate gridFsTemplate() throws Exception {
    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
  }

  @Override
  public Mongo mongo() throws Exception {
    return new MongoClient(mongoHost, mongoPort);
  }

  @Override
  protected String getDatabaseName() {
    return "_dvs";
  }

  public static void main(String[] args) {
    SpringApplication.run(Adrestia.class, args);
  }

}
