/*
Apache2 License Notice
Copyright 2018 Alex Barry
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

package com.ao.adrestia;

import com.ao.adrestia.filters.PersistenceFilter;
import com.ao.adrestia.filters.RoutingFilter;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication(exclude = {SolrAutoConfiguration.class})
public class AdrestiaApplication extends AbstractMongoConfiguration {

  // Hostname of Mongo Connection
  @Value("${server.mongo.host}")
  private String mongoHost;

  // Hostname of Mongo Port
  @Value("${server.mongo.port}")
  private int mongoPort;

  @Override
  public MongoClient mongoClient() {
    return new MongoClient(mongoHost, mongoPort);
  }

  @Override
  protected String getDatabaseName() {
    return "_adrestia";
  }

  public static void main(String[] args) {
    SpringApplication.run(AdrestiaApplication.class, args);
  }

  @Bean
  public RoutingFilter routingFilter() {
    return new RoutingFilter();
  }

  @Bean PersistenceFilter persistenceFilter() {
    return new PersistenceFilter();
  }
}
