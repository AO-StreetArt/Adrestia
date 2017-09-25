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

import adrestia.UserDevice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Scene {

  @JsonProperty("key")
  private String key;
  @JsonProperty("name")
  private String name;
  @JsonProperty("region")
  private String region;
  @JsonProperty("latitude")
  private double latitude;
  @JsonProperty("longitude")
  private double longitude;
  @JsonProperty("distance")
  private double distance;
  @JsonProperty("asset_ids")
  private String[] assets;
  @JsonProperty("tags")
  private String[] tags;
  @JsonProperty("devices")
  private UserDevice[] device_list;

  /**
	* Default empty Scene constructor
	*/
	public Scene() {
		super();
	}

	/**
	* Complete Scene constructor
	*/
	public Scene(String key, String name, String region, double latitude,
    double longitude, double distance, String[] assets, String[] tags, UserDevice[] devices) {
      this.key = key;
      this.name = name;
      this.region = region;
      this.latitude = latitude;
      this.longitude = longitude;
      this.distance = distance;
      this.assets = assets;
      this.tags = tags;
      this.device_list = devices;
	}

	/**
	* Returns value of key
	* @return
	*/
  @JsonGetter("key")
	public String getKey() {
		return this.key;
	}

  /**
	* Set value of key
	* @return
	*/
  @JsonSetter("key")
	public void setKey(String newKey) {
		this.key = newKey;
	}

	/**
	* Returns value of name
	* @return
	*/
  @JsonGetter("name")
	public String getName() {
		return this.name;
	}

  /**
	* Set value of name
	* @return
	*/
  @JsonSetter("name")
	public void setName(String newName) {
		this.name = newName;
	}

	/**
	* Returns value of region
	* @return
	*/
  @JsonGetter("region")
	public String getRegion() {
		return this.region;
	}

  /**
	* Set value of region
	* @return
	*/
  @JsonSetter("region")
	public void setRegion(String newRegion) {
		this.region = newRegion;
	}

	/**
	* Returns value of latitude
	* @return
	*/
  @JsonGetter("latitude")
	public double getLatitude() {
		return this.latitude;
	}

  /**
	* Set value of latitude
	* @return
	*/
  @JsonSetter("latitude")
	public void setLatitude(double newLatitude) {
		this.latitude = newLatitude;
	}

	/**
	* Returns value of longitude
	* @return
	*/
  @JsonGetter("longitude")
	public double getLongitude() {
		return this.longitude;
	}

  /**
	* Set value of longitude
	* @return
	*/
  @JsonSetter("longitude")
	public void setLongitude(double newLongitude) {
		this.longitude = newLongitude;
	}

	/**
	* Returns value of distance
	* @return
	*/
  @JsonGetter("distance")
	public double getDistance() {
		return this.distance;
	}

  /**
	* Set value of distance
	* @return
	*/
  @JsonSetter("distance")
	public void setDistance(double newDistance) {
		this.distance = newDistance;
	}

	/**
	* Returns the assets of the scene
	* @return
	*/
  @JsonGetter("asset_ids")
	public String[] getAssets() {
		return this.assets;
	}

  /**
	* Set the assets of the scene
	* @return
	*/
  @JsonSetter("asset_ids")
	public void setAssets(String[] newAssets) {
		this.assets = newAssets;
	}

	/**
	* Returns the tags of the Scene
	* @return
	*/
  @JsonGetter("tags")
	public String[] getTags() {
		return this.tags;
	}

  /**
	* Set the tags of the Scene
	* @return
	*/
  @JsonSetter("tags")
	public void setTags(String[] newTags) {
		this.tags = newTags;
	}

  /**
	* Returns the devices of the Scene
	* @return
	*/
  @JsonGetter("devices")
	public UserDevice[] getDevices() {
		return this.device_list;
	}

  /**
	* Set the devices of the Scene
	* @return
	*/
  @JsonSetter("devices")
	public void getTags(UserDevice[] newDevices) {
		this.device_list = newDevices;
	}
}
