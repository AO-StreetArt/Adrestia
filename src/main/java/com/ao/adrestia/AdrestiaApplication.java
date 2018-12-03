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
import com.ao.adrestia.security.TokenAuthentication;

import com.auth0.AuthenticationController;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableRetry
@EnableDiscoveryClient
@EnableZuulProxy
@EnableWebSecurity
@EnableAutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(AdrestiaMongoConfiguration.class)
@PropertySource(value="file:/auth/auth0.properties", ignoreResourceNotFound=true)
@PropertySource(value="classpath:auth0.properties", ignoreResourceNotFound=true)
@SpringBootApplication(exclude = {SolrAutoConfiguration.class})
public class AdrestiaApplication extends WebSecurityConfigurerAdapter {

  /**
  * This is your auth0 domain
  * (tenant you have created when registering with auth0 - account name).
  */
  @Value(value = "${com.auth0.domain}")
  private String domain;

  /**
  * This is the client id of your auth0 application
  * (see Settings page on auth0 dashboard).
  */
  @Value(value = "${com.auth0.clientId:}")
  private String clientId;

  /**
  * This is the client secret of your auth0 application
  * (see Settings page on auth0 dashboard).
  */
  @Value(value = "${com.auth0.clientSecret:}")
  private String clientSecret;

  // Is Authentication required for accessing our HTTP Server
  @Value("${server.auth.active:false}")
  private boolean httpAuthActive;

  @Bean
  public InternalResourceViewResolver viewResolver() {
    InternalResourceViewResolver viewResolver
        = new InternalResourceViewResolver();
    viewResolver.setViewClass(JstlView.class);
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");
    return viewResolver;
  }

  @Bean
  public AuthenticationController authenticationController() throws UnsupportedEncodingException {
    return AuthenticationController.newBuilder(domain, clientId, clientSecret)
        .build();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    if (httpAuthActive) {
      http.authorizeRequests()
          .antMatchers("/callback", "/login", "/health").permitAll()
          .antMatchers("/**").authenticated()
          .and()
          .logout().permitAll();
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    } else {
      // Security Disabled
      http.authorizeRequests().anyRequest().permitAll();
    }
  }

  public String getDomain() {
    return domain;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
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
