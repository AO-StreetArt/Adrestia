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

package com.ao.adrestia.cache;

import com.ao.adrestia.model.SceneLocation;
import com.ao.adrestia.repo.SceneLocationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

/**
* A Thread-safe Cache for Scene Locations.
*
* <p>Discovery Component can call get() to find services in the cache, if available.
* Then, it can call add() to push additional elements into the cache.
* Cache elements can be updated periodically with the load() method, to stay up to date.</p>
*/
@Component
public class SceneLocationCache {

  private static Logger log = LoggerFactory.getLogger("adrestia.SceneLocationCache");

  @Autowired
  SceneLocationRepository dbScenes;

  private ConcurrentLinkedQueue<String> containedIds = new ConcurrentLinkedQueue<String>();

  // Map of scene Id to a list of locations
  private ConcurrentHashMap<String, List<SceneLocation>> sceneMap =
      new ConcurrentHashMap<String, List<SceneLocation>>();

  // Maximum Number of scenes in the Cache
  @Value("${cache.max_scenes}")
  private int maxSceneCount;

  // Get a list of Locations for a scene
  public List<SceneLocation> get(String sceneId) {
    return sceneMap.get(sceneId);
  }

  /**
   * Should be called by discovery service when a new scene is encountered.
   */
  public void add(String sceneId, List<SceneLocation> newLocations) {
    log.info("Attempting to add Scene to Cache {}", sceneId);
    if (!(containedIds.contains(sceneId))) {
      if (containedIds.size() > maxSceneCount) {
        String lastIds = containedIds.peek();
        if (lastIds != null) {
          sceneMap.remove(lastIds);
        }
        containedIds.poll();
      }
      containedIds.add(sceneId);
      sceneMap.put(sceneId, newLocations);
    }
  }

  /**
   * Can be called by a bg thread to keep cache up to date.
   */
  public void load() {
    // Load the scenes out of the DB
    for (String id : containedIds) {
      log.info("Loading latest info into Cache for Scene {}", id);
      List<SceneLocation> newLocations = new ArrayList<SceneLocation>();
      for (SceneLocation loc : dbScenes.findBySceneIdentifier(id)) {
        newLocations.add(loc);
      }
      sceneMap.put(id, newLocations);
    }
  }
}
