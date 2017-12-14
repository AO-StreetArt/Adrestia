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

import adrestia.ObjectDocument;
import adrestia.ObjectList;

import org.springframework.stereotype.Service;

/**
* Data Access for Objects.
*/
@Service
public interface ObjectDao {
  /**
  * Create a ObjectDocument.
  * @param inpDocument A ObjectDocument Object to create
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList create(ObjectDocument inpDocument);

  /**
  * Update a ObjectDocument.
  * @param inpDocument A ObjectDocument Object to save
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList update(ObjectDocument inpDocument);

  /**
  * Retrieve a ObjectDocument.
  * @param docKey The key of the doc to retrieve
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList get(String docKey);

  /**
  * Remove a ObjectDocument.
  * @param docKey The key of the ObjectDocument to destroy
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList destroy(String docKey);

  /**
  * Query for one or more Scenes.
  * @param inpDocument A ObjectDocument Object, whos fields represent the desired query
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList query(ObjectDocument inpDocument);

  /**
  * Create a ObjectDocument.
  * @param docKey The name of the doc to get a lock on
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList lock(String docKey);

  /**
  * Update a ObjectDocument.
  * @param docKey The name of the doc to release a lock on
  * @return  A ObjectList object, returned from the service implementing the DAO
  */
  public ObjectList unlock(String docKey);
}
