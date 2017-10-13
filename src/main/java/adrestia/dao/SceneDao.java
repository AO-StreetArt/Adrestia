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
  */
  public SceneList create(Scene inpScene);

  /**
  * Update a Scene.
  */
  public SceneList update(Scene inpScene);

  /**
  * Retrieve a Scene.
  */
  public SceneList get(String sceneName);

  /**
  * Remove a Scene.
  */
  public SceneList destroy(String sceneName);

  /**
  * Query for one or more Scenes.
  */
  public SceneList query(Scene inpScene);

  /**
  * Register a Device to a Scene.
  */
  public SceneList register(String sceneName, String deviceId, Transform inpTransform);

  /**
  * Deregister a Device from a Scene.
  */
  public SceneList deregister(String sceneName, String deviceId);

  /**
  * Synchronize an Object and Scene transformation.
  */
  public SceneList synchronize(String sceneName, String deviceId, Transform inpTransform);
}
