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

package com.ao.adrestia.discovery;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

/**
* Finds Instances of the Aesel Components.
*/
@Service
public interface AeselDiscoveryService {

  /**
  * Find an instance of Crazy Ivan.
  * @return A ServiceInstance object with the instance details found
  */
  public ServiceInstance findCrazyIvan();

  /**
  * Find an instance of CLyman.
  * @return A ServiceInstance object with the instance details found
  */
  public ServiceInstance findClyman(String sceneId);

  /**
  * Find an instance of AVC.
  * @return A ServiceInstance object with the instance details found
  */
  public ServiceInstance findAvc();

  /**
  * Find an instance of the Project Service.
  * @return A ServiceInstance object with the instance details found
  */
  public ServiceInstance findProjectService();

  /**
  * Find instances of Crazy Ivan in a particular cluster.
  * @return A ServiceInstance object with the instance details found
  */
  public List<ServiceInstance> findCrazyIvanByCluster(String clusterId);

  /**
  * Find instances of CLyman in a particular cluster.
  * @return A ServiceInstance object with the instance details found
  */
  public List<ServiceInstance> findClymanByCluster(String clusterId);
}
