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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Transform {

  @JsonProperty("translation")
  private double[] translation;

  @JsonProperty("rotation")
  private double[] rotation;

	/**
	* Default empty Transform constructor
	*/
	public Transform() {
		super();
	}

	/**
	* Default Transform constructor
	*/
	public Transform(double[] newTranslation, double[] newRotation) {
		super();
		this.translation = newTranslation;
		this.rotation = newRotation;
	}

	/**
	* Returns value of translation
	* @return
	*/
  @JsonGetter("translation")
	public double[] getTranslation() {
		return this.translation;
	}

	/**
	* Returns value of rotation
	* @return
	*/
  @JsonGetter("rotation")
	public double[] getRotation() {
		return this.rotation;
	}

	/**
	* Sets new value of translation
	* @param
	*/
  @JsonSetter("translation")
	public void setTranslation(double[] newTranslation) {
		this.translation = newTranslation;
	}

	/**
	* Sets new value of rotation
	* @param
	*/
  @JsonSetter("rotation")
	public void setRotation(double[] newRotation) {
		this.rotation = newRotation;
	}
}
