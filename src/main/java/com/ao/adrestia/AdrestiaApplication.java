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
import com.ao.adrestia.model.ApplicationUser;
import com.ao.adrestia.repo.ApplicationUserRepository;
import com.ao.adrestia.security.JWTAuthorizationFilter;
import com.ao.adrestia.security.JWTAuthenticationFilter;
import com.ao.adrestia.security.DbUserDetails;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableRetry
@EnableDiscoveryClient
@EnableZuulProxy
@EnableWebSecurity
@Import(AdrestiaMongoConfiguration.class)
@SpringBootApplication(exclude = {SolrAutoConfiguration.class})
public class AdrestiaApplication extends WebSecurityConfigurerAdapter {
  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  // Is Authentication required for accessing our HTTP Server
  @Value("${server.auth.active:false}")
  private boolean httpAuthActive;

  // Secret for generating JWTs
  @Value("${server.auth.secret:SecretKeyToGenJWTs}")
  private String jwtSecret;

  @Autowired
  DbUserDetails userDetailsService;

  @Autowired
  ApplicationUserRepository applicationUserRepository;

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
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return encoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();

    if (httpAuthActive) {
      http.authorizeRequests()
          .antMatchers("/health").permitAll()
          .antMatchers("/portal/login").permitAll()
          .antMatchers(HttpMethod.POST, "/login").permitAll()
          .antMatchers("/**").authenticated()
          .and()
          .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtSecret))
          .addFilter(new JWTAuthorizationFilter(authenticationManager(), applicationUserRepository, jwtSecret))
          // this disables session creation on Spring Security
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    } else {
      // Security Disabled
      http.authorizeRequests().anyRequest().permitAll();
    }
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    if (httpAuthActive) {
      // Create a default account
      auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }
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
