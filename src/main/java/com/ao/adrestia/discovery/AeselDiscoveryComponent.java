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

package com.ao.adrestia.discovery;

import com.ao.adrestia.cache.SceneLocationCache;
import com.ao.adrestia.model.SceneLocation;
import com.ao.adrestia.repo.SceneLocationRepository;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
* Used by Zuul filters to access Service information.
*/
@Component
public class AeselDiscoveryComponent implements AeselDiscoveryService, CommandLineRunner  {

  private AtomicInteger ivanIndex = new AtomicInteger(0);
  private AtomicInteger clymanIndex = new AtomicInteger(0);
  private AtomicInteger avcIndex = new AtomicInteger(0);

  // Activate Service Discovery
  @Value("${service.discovery.active}")
  private String discoveryActive;

  // Crazy Ivan Service Name
  @Value("${service.ivan.name}")
  private String ivanServiceName;

  // Crazy Ivan Service Host
  @Value("${service.ivan.host}")
  private String ivanServiceHost;

  // Crazy Ivan Service Port
  @Value("${service.ivan.port}")
  private int ivanServicePort;

  // Clyman Service Name
  @Value("${service.clyman.name}")
  private String clymanServiceName;

  // Clyman Service Host
  @Value("${service.clyman.host}")
  private String clymanServiceHost;

  // Clyman Service Port
  @Value("${service.clyman.port}")
  private int clymanServicePort;

  // Avc Service Name
  @Value("${service.avc.name}")
  private String avcServiceName;

  // Avc Service Host
  @Value("${service.avc.host}")
  private String avcServiceHost;

  // Avc Service Port
  @Value("${service.avc.port}")
  private int avcServicePort;

  // Refresh Interval
  @Value("${service.refresh.interval}")
  private int refreshInterval;

  private static Logger log = LoggerFactory.getLogger("adrestia.AeselDiscoveryComponent");

  // Consul Connection
  @Autowired
  DiscoveryClient consulClient;

  // In-memory Scene Cache
  @Autowired
  SceneLocationCache sceneCache;

  // Mongo Connection for looking up scene information
  @Autowired
  SceneLocationRepository dbScenes;

  private List<ServiceInstance> clusterTransaction(String serviceName, String clusterId) {
    // Find a list of Crazy Ivan instances from Consul
    List<ServiceInstance> instances = consulClient.getInstances(serviceName);

    if (instances.size() == 0) {
      log.error("No Instances found");
      return instances;
    }

    // Return only those instances which have been tagged with the
    // specified cluster ID.
    List<ServiceInstance> returnList = new ArrayList<ServiceInstance>();
    for (ServiceInstance instance : instances) {
      Map<String,String> tags = instance.getMetadata();
      if (tags.containsKey("cluster")) {
        if (tags.get("cluster").equals(clusterId)) {
          returnList.add(instance);
        }
      } else {
        log.warn("Unable to find cluster tag on {} instance: {}", serviceName, instance.getHost());
      }
    }
    return returnList;
  }

  /**
  * Find an instance of Crazy Ivan.
  * This can be done with just pure round robin.
  * @return A ServiceInstance object with the instance details found
  */
  @Override
  public ServiceInstance findCrazyIvan() {
    log.info("Finding Crazy Ivan Instance");

    if (discoveryActive.equals("true")) {
      // Find a list of Crazy Ivan instances from Consul
      List<ServiceInstance> instances = consulClient.getInstances(ivanServiceName);

      if (instances.size() == 0) {
        log.error("No Instances found");
        return null;
      }

      // Determine the target index
      int targetIndex = ivanIndex.getAndIncrement();
      if (ivanIndex.compareAndSet(instances.size(), 0)) {
        targetIndex = 0;
      } else {
        targetIndex = ivanIndex.getAndIncrement();
      }

      log.info("Returning Ivan instance {} of {} retrieved from Consul", targetIndex, instances.size());

      // Return the targeted value
      return instances.get(targetIndex);
    } else {
      log.info("Returning Ivan instance at {}:{}", ivanServiceHost, ivanServicePort);
      return new DefaultServiceInstance(ivanServiceName, ivanServiceHost, ivanServicePort, false);
    }
  }

  /**
  * Find an instance of CLyman.
  * This must be done according to the Scene-Cluster Map.
  * @return A ServiceInstance object with the instance details found
  */
  @Override
  public ServiceInstance findClyman(String sceneId) {
    log.info("Finding Clyman Instance");

    if (discoveryActive.equals("true")) {
      // Determine the target index
      int targetIndex = clymanIndex.getAndIncrement();

      // Look for the Scene in the Cache
      List<SceneLocation> cachedLocations = sceneCache.get(sceneId);
      if (cachedLocations != null && cachedLocations.size() > 0) {

        // Get the cluster name out of our cache response
        SceneLocation targetLocation = cachedLocations.get(0);
        log.info("Retrieved Scene Location out of cache at {}", targetLocation.clusterIdentifier);

        // Now that we have a cluster, find an Ivan instance
        List<ServiceInstance> activeInstances = findClymanByCluster(targetLocation.clusterIdentifier);
        if (activeInstances.size() > 0) {
          if (clymanIndex.compareAndSet(activeInstances.size(), 0)) {
            targetIndex = clymanIndex.getAndIncrement();
          }
          return activeInstances.get(targetIndex);
        }
      } else {
        // If not present in the cache, look for the scene in the DB
        List<SceneLocation> dbLocations = dbScenes.findBySceneIdentifier(sceneId);
        if (clymanIndex.compareAndSet(dbLocations.size(), 0)) {
          targetIndex = clymanIndex.getAndIncrement();
        }
        if (dbLocations.size() > 0) {
          log.info("Retrieved {} Ivan instance from DB", dbLocations.size());
          // load the locations into the cache
          sceneCache.add(sceneId, dbLocations);
          // Return the target location
          SceneLocation targetLocation = dbLocations.get(targetIndex);

          // Now that we have the cache, find the Crazy Ivan instance
          List<ServiceInstance> activeInstances = findClymanByCluster(targetLocation.clusterIdentifier);
          if (activeInstances.size() > 0) {
            if (clymanIndex.compareAndSet(activeInstances.size(), 0)) {
              targetIndex = clymanIndex.getAndIncrement();
            }
            return activeInstances.get(targetIndex);
          }
        } else {
          log.error("No Instances Found");
          return null;
        }
      }
    } else {
      return new DefaultServiceInstance(
          clymanServiceName, clymanServiceHost, clymanServicePort, false);
    }
    return null;
  }

  /**
  * Find an instance of Avc.
  * This can be done by pure round-robin.
  * @return A ServiceInstance object with the instance details found
  */
  @Override
  public ServiceInstance findAvc() {
    log.info("Finding Avc Instance");

    if (discoveryActive.equals("true")) {
      // Find a list of Crazy Ivan instances from Consul
      List<ServiceInstance> instances = consulClient.getInstances(avcServiceName);

      if (instances.size() == 0) {
        log.error("No Instances found");
        return null;
      }

      // Determine the target index
      int targetIndex = avcIndex.getAndIncrement();
      if (avcIndex.compareAndSet(instances.size(), 0)) {
        targetIndex = avcIndex.getAndIncrement();
      }

      // Return the targeted value
      return instances.get(targetIndex);
    } else {
      return new DefaultServiceInstance(avcServiceName, avcServiceHost, avcServicePort, false);
    }
  }

  /**
  * Find instances of Crazy Ivan in a particular cluster.
  * @return A ServiceInstance object with the instance details found
  */
  @Override
  public List<ServiceInstance> findCrazyIvanByCluster(String clusterId) {
    if (discoveryActive.equals("true")) {
      return clusterTransaction(ivanServiceName, clusterId);
    } else {
      List<ServiceInstance> returnList = new ArrayList<ServiceInstance>();
      returnList.add(new DefaultServiceInstance(
          ivanServiceName, ivanServiceHost, ivanServicePort, false));
      return returnList;
    }
  }

  /**
  * Find instances of CLyman in a particular cluster.
  * @return A ServiceInstance object with the instance details found
  */
  @Override
  public List<ServiceInstance> findClymanByCluster(String clusterId) {
    if (discoveryActive.equals("true")) {
      return clusterTransaction(clymanServiceName, clusterId);
    } else {
      List<ServiceInstance> returnList = new ArrayList<ServiceInstance>();
      returnList.add(new DefaultServiceInstance(
          clymanServiceName, clymanServiceHost, clymanServicePort, false));
      return returnList;
    }
  }

  /**
  * Start up a background thread for Updating our Scene Cache.
  */
  @Override
  public void run(String... args) throws InterruptedException, Exception {
    // Define the runnable that will actually update the cache
    Runnable r = new Runnable() {
      @Override public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
          sceneCache.load();
          try {
            Thread.sleep(refreshInterval);
          } catch (InterruptedException e) {
            log.warn("Discovery service shutting down");
            keepRunning = false;
          }
        }
      }
    };

    // Start the background thread to keep the cache up to date
    Thread loaderThread = new Thread(r);
    loaderThread.setDaemon(true);
    loaderThread.start();
  }
}
