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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
* Represents a Scene (A Logical grouping of objects, and a coordinate system).
*/
public class Scene {

  private String key;
  private String name;
  private String region;
  private boolean active;
  private double latitude;
  private double longitude;
  private double distance;
  @JsonProperty("asset_ids")
  private String[] assets = new String[0];
  private String[] tags = new String[0];
  @JsonProperty("devices")
  private UserDevice[] deviceList = new UserDevice[0];

  /**
  * Default empty Scene constructor.
  */
  public Scene() {
    super();
    this.latitude = -9999.0;
    this.longitude = -9999.0;
    this.distance = 0.0;
    this.key = "";
    this.name = "";
    this.region = "";
    this.active = false;
  }

  /**
  * Complete Scene constructor.
  * @param key A Unique String Key for the Scene.
  * @param name A Name for the scene, by which it is externally identified.
  * @param region A String Region for the Scene.
  * @param latitude The latitude of the scene.
  * @param longitude The longitude of the scene.
  * @param active Is the scene active or not.
  * @param distance The distance from the provided lat/long, used for queries.
  * @param assets A String Array of Asset ID's associated to the scene.
  * @param tags A String Array of Tags for the scene, used for queries.
  * @param devices An Array of User Devices registered to the scene.
  */
  public Scene(String key, String name, String region, double latitude,
      double longitude, boolean active, double distance, String[] assets,
      String[] tags, UserDevice[] devices) {
    this.key = key;
    this.name = name;
    this.region = region;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distance = distance;
    this.assets = assets;
    this.tags = tags;
    this.active = active;
    this.deviceList = devices;
  }

  /**
  * Partial Scene constructor.
  * @param key A Unique String Key for the Scene.
  * @param name A Name for the scene, by which it is externally identified.
  * @param region A String Region for the Scene.
  * @param latitude The latitude of the scene.
  * @param longitude The longitude of the scene.
  * @param distance The distance from the provided lat/long, used for queries.
  * @param assets A String Array of Asset ID's associated to the scene.
  * @param tags A String Array of Tags for the scene, used for queries.
  * @param devices An Array of User Devices registered to the scene.
  */
  public Scene(String key, String name, String region, double latitude,
      double longitude, double distance, String[] assets,
      String[] tags, UserDevice[] devices) {
    this.key = key;
    this.name = name;
    this.region = region;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distance = distance;
    this.assets = assets;
    this.tags = tags;
    this.active = true;
    this.deviceList = devices;
  }

  /**
  * Returns value of key.
  * @return The Unique String Key for the Scene.
  */
  @JsonGetter("key")
  public String getKey() {
    return this.key;
  }

  /**
  * Set value of key.
  * @param newKey A Unique String Key for the Scene.
  */
  @JsonSetter("key")
  public void setKey(String newKey) {
    this.key = newKey;
  }

  /**
  * Set value of active.
  * @param newActive A boolean value representing if the scene is active.
  */
  @JsonSetter("active")
  public void setActive(boolean newActive) {
    this.active = newActive;
  }

  /**
  * Returns value of active.
  * @return True if the scene is active, otherwise false.
  */
  @JsonGetter("active")
  public boolean getActive() {
    return this.active;
  }

  /**
  * Returns value of name.
  * @return The Name of the scene (external identifier).
  */
  @JsonGetter("name")
  public String getName() {
    return this.name;
  }

  /**
  * Set value of name.
  * @param newName A Name for the scene, by which it is externally identified.
  */
  @JsonSetter("name")
  public void setName(String newName) {
    this.name = newName;
  }

  /**
  * Returns value of region.
  * @return The Region of the Scene.
  */
  @JsonGetter("region")
  public String getRegion() {
    return this.region;
  }

  /**
  * Set value of region.
  * @param newRegion A String Region for the Scene.
  */
  @JsonSetter("region")
  public void setRegion(String newRegion) {
    this.region = newRegion;
  }

  /**
  * Returns value of latitude.
  * @return The latitude of the scene.
  */
  @JsonGetter("latitude")
  public double getLatitude() {
    return this.latitude;
  }

  /**
  * Set value of latitude.
  * @param newLatitude The latitude of the scene.
  */
  @JsonSetter("latitude")
  public void setLatitude(double newLatitude) {
    this.latitude = newLatitude;
  }

  /**
  * Returns value of longitude.
  * @return The longitude of the scene.
  */
  @JsonGetter("longitude")
  public double getLongitude() {
    return this.longitude;
  }

  /**
  * Set value of longitude.
  * @param newLongitude The longitude of the scene.
  */
  @JsonSetter("longitude")
  public void setLongitude(double newLongitude) {
    this.longitude = newLongitude;
  }

  /**
  * Returns value of distance.
  * @return The distance from the provided lat/long, used for queries.
  */
  @JsonGetter("distance")
  public double getDistance() {
    return this.distance;
  }

  /**
  * Set value of distance.
  * @param newDistance The distance from the provided lat/long, for queries.
  */
  @JsonSetter("distance")
  public void setDistance(double newDistance) {
    this.distance = newDistance;
  }

  /**
  * Returns the assets of the scene.
  * @return A String Array of Asset ID's associated to the scene.
  */
  @JsonGetter("asset_ids")
  public String[] getAssets() {
    return this.assets;
  }

  /**
  * Set the assets of the scene.
  * @param newAssets A String Array of Asset ID's associated to the scene.
  */
  @JsonSetter("asset_ids")
  public void setAssets(String[] newAssets) {
    this.assets = newAssets;
  }

  /**
  * Returns the tags of the Scene.
  * @return A String Array of Tags for the scene, used for queries.
  */
  @JsonGetter("tags")
  public String[] getTags() {
    return this.tags;
  }

  /**
  * Set the tags of the Scene.
  * @param newTags A String Array of Tags for the scene, used for queries.
  */
  @JsonSetter("tags")
  public void setTags(String[] newTags) {
    this.tags = newTags;
  }

  /**
  * Returns the devices of the Scene.
  * @return An Array of User Devices registered to the scene.
  */
  @JsonGetter("devices")
  public UserDevice[] getDevices() {
    return this.deviceList;
  }

  /**
  * Set the devices of the Scene.
  * @param newDevices An Array of User Devices registered to the scene.
  */
  @JsonSetter("devices")
  public void setDevices(UserDevice[] newDevices) {
    this.deviceList = newDevices;
  }
}
