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

package adrestia.model.scene;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
* Represents a Device viewing objects (ie. a mobile phone).
*/
public class UserDevice {

  private String key;
  private String host;
  private int port;
  private Transform transform;


  /**
  * Default empty UserDevice constructor.
  */
  public UserDevice() {
    super();
  }

  /**
  * Default UserDevice constructor.
  * @param key       The Unique String Key of the Device.
  * @param host      The hostname of the device for UDP Communications
  * @param port      The port of the device for UDP Communications
  * @param transform A Transformation Object for the device to store.
  */
  public UserDevice(String key, String host, int port, Transform transform) {
    super();
    this.key = key;
    this.host = host;
    this.port = port;
    this.transform = transform;
  }

  /**
  * Returns value of key.
  * @return The Unique String Key of the Device.
  */
  @JsonGetter("key")
  public String getKey() {
    return this.key;
  }

  /**
  * Returns value of host.
  * @return The Unique String host of the Device.
  */
  @JsonGetter("hostname")
  public String getHost() {
    return this.host;
  }

  /**
  * Returns value of port.
  * @return The Unique Integer port of the Device.
  */
  @JsonGetter("port")
  public int getPort() {
    return this.port;
  }

  /**
  * Returns value of transform.
  * @return The Transformation Object stored in the User Device.
  */
  @JsonGetter("transform")
  public Transform getTransform() {
    return this.transform;
  }

  /**
  * Sets new value of key.
  * @param key A Unique String key for the device.
  */
  @JsonSetter("key")
  public void setKey(String key) {
    this.key = key;
  }

  /**
  * Sets new value of host.
  * @param host A Unique String host for the device.
  */
  @JsonSetter("host")
  public void setHost(String host) {
    this.host = host;
  }

  /**
  * Sets new value of port.
  * @param port A Unique int port for the device.
  */
  @JsonSetter("port")
  public void setPort(int port) {
    this.port = port;
  }

  /**
  * Sets new value of transform.
  * @param transform A Transformation Object for the device to store.
  */
  @JsonSetter("transform")
  public void setTransform(Transform transform) {
    this.transform = transform;
  }
}
