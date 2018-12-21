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

package com.ao.adrestia.security;

import com.ao.adrestia.model.ApplicationUser;
import com.ao.adrestia.repo.ApplicationUserRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeedRunner implements CommandLineRunner {
  private static Logger log = LoggerFactory.getLogger("adrestia.UserSeedRunner");
  // Is Authentication required for accessing our HTTP Server
  @Value("${server.auth.active:false}")
  private boolean httpAuthActive;
  @Value("${server.auth.user:aesel}")
  private String initUser;
  @Value("${server.auth.pw:password}")
  private String initPw;

  @Autowired
  ApplicationUserRepository applicationUserRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public void run(String...args) throws Exception {
    // Create the initial user
    if (httpAuthActive) {
      List<ApplicationUser> existingUsers =
          applicationUserRepository.findByUsername(initUser);
      if (existingUsers.size() == 0) {
        log.info("Seeding Initial Application User");
        ApplicationUser newUser = new ApplicationUser();
        newUser.username = initUser;
        newUser.password = bCryptPasswordEncoder.encode(initPw);
        newUser.isAdmin = true;
        newUser.isActive = true;
        applicationUserRepository.save(newUser);
      } else {
        log.debug("Initial Application User creation bypassed");
      }
    }
  }

}
