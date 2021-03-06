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

package com.ao.adrestia.repo;

import com.ao.adrestia.model.SceneLocation;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SceneLocationRepository extends MongoRepository<SceneLocation, String> {

  public List<SceneLocation> findBySceneIdentifier(String sceneIdentifier);
  
  public List<SceneLocation> findByClusterIdentifier(String clusterIdentifier);

}
