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

import adrestia.Scene;

public class SceneList {

  private final int msg_type;
  private final long num_records;
  private final Scene[] scene_list;


	/**
	* Default SceneList constructor
	*/
	public SceneList(int msg_type, long num_records, Scene[] scene_list) {
    this.msg_type = msg_type;
    this.num_records = num_records;
    this.scene_list = scene_list;
	}

	/**
	* Returns value of msg_type
	* @return
	*/
	public int getMsgType() {
		return this.msg_type;
	}

	/**
	* Returns value of num_records
	* @return
	*/
	public long getNumRecords() {
		return this.num_records;
	}

	/**
	* Returns the Scene list
	* @return
	*/
	public Scene[] getSceneList() {
		return this.scene_list;
	}
}
