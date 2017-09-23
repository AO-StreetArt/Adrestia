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

public class Scene {

  private final String key;
  private final String name;
  private final String region;
  private final double latitude;
  private final double longitude;
  private final double distance;
  private final String[] assets;
  private final String[] tags;

	/**
	* Default Scene constructor
	*/
	public Scene(String key, String name, String region, double latitude,
    double longitude, double distance, String[] assets, String[] tags) {
      this.key = key;
      this.name = name;
      this.region = region;
      this.latitude = latitude;
      this.longitude = longitude;
      this.distance = distance;
      this.assets = assets;
      this.tags = tags;
	}

	/**
	* Returns value of key
	* @return
	*/
	public String getKey() {
		return this.key;
	}

	/**
	* Returns value of name
	* @return
	*/
	public String getName() {
		return this.name;
	}

	/**
	* Returns value of region
	* @return
	*/
	public String getRegion() {
		return this.region;
	}

	/**
	* Returns value of latitude
	* @return
	*/
	public double getLatitude() {
		return this.latitude;
	}

	/**
	* Returns value of longitude
	* @return
	*/
	public double getLongitude() {
		return this.longitude;
	}

	/**
	* Returns value of distance
	* @return
	*/
	public double getDistance() {
		return this.distance;
	}

	/**
	* Returns the assets of the scene
	* @return
	*/
	public String[] getAssets() {
		return this.assets;
	}

	/**
	* Returns the tags of the Scene
	* @return
	*/
	public String[] getTags() {
		return this.tags;
	}
}
