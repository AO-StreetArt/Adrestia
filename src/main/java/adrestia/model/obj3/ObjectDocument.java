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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
* Represents an Object in 3-space.
* our transforms represent the transform away from the object's current position,
* when it is recieved from the client.  When it is recieved from CLyman,
* (ie. in response to a get message) it represents the overall transform amount.
*/
public class ObjectDocument {

  private String key;
  private String name;
  private String type;
  private String subtype;
  private String owner;
  private String scene;
  private double[] translation = new double[0];
  @JsonProperty("rotation_euler")
  private double[] rotationEuler = new double[0];
  private double[] scale = new double[0];
  private String[] assets = new String[0];
  private double[] transform = new double[0];

  /**
  * Default empty ObjectDocument constructor.
  */
  public ObjectDocument() {
    super();
  }

  /**
  * Default ObjectDocument constructor.
  * @param key The unique ID of the Object
  * @param name The name of the Object
  * @param type The type of the Object
  * @param subtype The specific subtype of the Object
  * @param owner The owner of the Object
  * @param translation The current translation of the Object
  * @param rotationEuler The current rotation of the Object
  * @param scale The scale of the Object
  * @param assets The asset list of the Object
  * @param transform The transform of the object
  */
  public ObjectDocument(String key, String name, String type, String subtype, String owner, String scene,
      double[] translation, double[] rotationEuler, double[] scale, String[] assets, double[] transform) {
    super();
    this.key = key;
    this.name = name;
    this.type = type;
    this.subtype = subtype;
    this.owner = owner;
    this.scene = scene;
    this.translation = translation;
    this.rotationEuler = rotationEuler;
    this.scale = scale;
    this.assets = assets;
    this.transform = transform;
  }

  /**
  * Returns value of key.
  * @return The unique ID of the Object
  */
  @JsonGetter("key")
  public String getKey() {
    return this.key;
  }

  /**
  * Returns value of name.
  * @return The name of the object
  */
  @JsonGetter("name")
  public String getName() {
    return this.name;
  }

  /**
  * Returns value of type.
  * @return The type of the object
  */
  @JsonGetter("type")
  public String getType() {
    return this.type;
  }

  /**
  * Returns value of subtype.
  * @return The subtype of the object
  */
  @JsonGetter("subtype")
  public String getSubtype() {
    return this.subtype;
  }

  /**
  * Returns value of owner.
  * @return The owner of the object
  */
  @JsonGetter("owner")
  public String getOwner() {
    return this.owner;
  }

  /**
  * Returns value of scene.
  * @return The scene of the object
  */
  @JsonGetter("scene")
  public String getScene() {
    return this.scene;
  }

  /**
  * Returns value of translation.
  * @return The current translation of the object
  */
  @JsonGetter("translation")
  public double[] getTranslation() {
    return this.translation;
  }

  /**
  * Returns value of rotationEuler.
  * @return The current euler rotation of the object
  */
  @JsonGetter("rotation_euler")
  public double[] getRotationEuler() {
    return this.rotationEuler;
  }

  /**
  * Returns value of scale.
  * @return The current scale of the object
  */
  public double[] getScale() {
    return this.scale;
  }

  /**
  * Returns value of assets.
  * @return The list of assets needed to render the object
  */
  @JsonGetter("assets")
  public String[] getAssets() {
    return this.assets;
  }

  /**
  * Returns value of transform.
  * @return The current transform of the object
  */
  @JsonGetter("transform")
  public double[] getTransform() {
    return this.transform;
  }

  /**
  * Sets new value of key.
  * @param key The key of the object
  */
  @JsonSetter("key")
  public void setKey(String key) {
    this.key = key;
  }

  /**
  * Sets new value of name.
  * @param name The name of the object
  */
  @JsonSetter("name")
  public void setName(String name) {
    this.name = name;
  }

  /**
  * Sets new value of type.
  * @param type The type of the object
  */
  @JsonSetter("type")
  public void setType(String type) {
    this.type = type;
  }

  /**
  * Sets new value of subtype.
  * @param subtype The subtype of the object
  */
  @JsonSetter("subtype")
  public void setSubtype(String subtype) {
    this.subtype = subtype;
  }

  /**
  * Sets new value of owner.
  * @param owner The owner of the object
  */
  @JsonSetter("owner")
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
  * Sets new value of scene.
  * @param scene The scene of the object
  */
  @JsonSetter("scene")
  public void setScene(String scene) {
    this.scene = scene;
  }

  /**
  * Sets new value of translation.
  * @param translation The current translation of the object
  */
  @JsonSetter("translation")
  public void setTranslation(double[] translation) {
    this.translation = translation;
  }

  /**
  * Sets new value of rotationEuler.
  * @param rotationEuler The current rotation of the object
  */
  @JsonSetter("rotation_euler")
  public void setRotationEuler(double[] rotationEuler) {
    this.rotationEuler = rotationEuler;
  }

  /**
  * Sets new value of scale.
  * @param scale The current scale of the object
  */
  @JsonSetter("scale")
  public void setScale(double[] scale) {
    this.scale = scale;
  }

  /**
  * Sets new value of assets.
  * @param assets The asset list for the object
  */
  @JsonSetter("assets")
  public void setAssets(String[] assets) {
    this.assets = assets;
  }

  /**
  * Sets new value of transform.
  * @param transform The current transform of the object
  */
  @JsonSetter("transform")
  public void setTransform(double[] transform) {
    this.transform = transform;
  }
}
