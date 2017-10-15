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

import org.springframework.stereotype.Service;

/**
* Data Access for Scenes.
*/
@Service
public interface SceneDao {

  /**
  * Create a Scene.
  * @param inpScene A Scene Object to create
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList create(Scene inpScene);

  /**
  * Update a Scene.
  * @param inpScene A Scene Object to save
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList update(Scene inpScene);

  /**
  * Retrieve a Scene.
  * @param sceneName The name of the scene to retrieve
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList get(String sceneName);

  /**
  * Remove a Scene.
  * @param sceneKey The key of the Scene to destroy
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList destroy(String sceneKey);

  /**
  * Query for one or more Scenes.
  * @param inpScene A Scene Object, whos fields represent the desired query
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList query(Scene inpScene);

  /**
  * Register a Device to a Scene.
  * @param sceneName The name of the scene being registered to
  * @param deviceId The key of the Device being registered
  * @param inpTransform The transform from Scene to Local Axis, can be null
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList register(String sceneName, String deviceId, Transform inpTransform);

  /**
  * Deregister a Device from a Scene.
  * @param sceneName The name of the scene being deregistered from
  * @param deviceId The key of the Device being deregistered
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList deregister(String sceneName, String deviceId);

  /**
  * Synchronize an Object and Scene transformation.
  * @param sceneName The name of the scene being registered to
  * @param deviceId The key of the Device being registered
  * @param inpTransform The corrected transform from Scene to Local Axis
  * @return  A SceneList object, returned from the service implementing the DAO
  */
  public SceneList synchronize(String sceneName, String deviceId, Transform inpTransform);
}
