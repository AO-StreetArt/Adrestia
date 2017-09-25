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
import adrestia.Scene;

public class SceneList {

  @JsonProperty("msg_type")
  private int msg_type;
  @JsonProperty("num_records")
  private long num_records;
  @JsonProperty("scenes")
  private Scene[] scene_list;
  @JsonProperty("err_code")
  private int error_code;
  @JsonProperty("err_msg")
  private String error_message;
  @JsonProperty("transaction_id")
  private String transaction_id;

  /**
	* Default empty SceneList constructor
	*/
	public SceneList() {
		super();
	}

	/**
	* Complete SceneList constructor
	*/
	public SceneList(int msg_type, long num_records, Scene[] scene_list, int err_code, String err_msg, String transaction_id) {
    this.msg_type = msg_type;
    this.num_records = num_records;
    this.scene_list = scene_list;
    this.error_code = err_code;
    this.error_message = err_msg;
    this.transaction_id = transaction_id;
	}

	/**
	* Returns value of msg_type
	* @return
	*/
  @JsonGetter("msg_type")
	public int getMsgType() {
		return this.msg_type;
	}

  /**
	* Set value of msg_type
	* @return
	*/
  @JsonSetter("msg_type")
	public void setMsgType(int newMsgType) {
		this.msg_type = newMsgType;
	}

	/**
	* Returns value of num_records
	* @return
	*/
  @JsonGetter("num_records")
	public long getNumRecords() {
		return this.num_records;
	}

  /**
	* Set value of num_records
	* @return
	*/
  @JsonSetter("num_records")
	public void setNumRecords(long newNumRecords) {
		this.num_records = newNumRecords;
	}

	/**
	* Returns the Scene list
	* @return
	*/
  @JsonGetter("scenes")
	public Scene[] getSceneList() {
		return this.scene_list;
	}

  /**
	* Set the Scene list
	* @return
	*/
  @JsonSetter("scenes")
	public void setSceneList(Scene[] newSceneList) {
		this.scene_list = newSceneList;
	}

	/**
	* Returns value of error_code
	* @return
	*/
  @JsonGetter("err_code")
	public int getErrorCode() {
		return this.error_code;
	}

  /**
	* Set value of error_code
	* @return
	*/
  @JsonSetter("err_code")
	public void setErrorCode(int newErrorCode) {
		this.error_code = newErrorCode;
	}

	/**
	* Returns value of error_message
	* @return
	*/
  @JsonGetter("err_msg")
	public String getErrorMessage() {
		return this.error_message;
	}

  /**
	* Set value of error_message
	* @return
	*/
  @JsonSetter("err_msg")
	public void setErrorMessage(String newErrorMessage) {
		this.error_message = newErrorMessage;
	}

  /**
	* Returns value of error_message
	* @return
	*/
  @JsonGetter("transaction_id")
	public String getTransactionId() {
		return this.transaction_id;
	}

  /**
	* Set value of error_message
	* @return
	*/
  @JsonSetter("transaction_id")
	public void setTransactionId(String newTransactionId) {
		this.transaction_id = newTransactionId;
	}
}
