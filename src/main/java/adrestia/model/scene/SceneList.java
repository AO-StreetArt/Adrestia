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
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
* Represents a List of Scenes, Required for communication with Crazy Ivan.
*/
public class SceneList {

  @JsonProperty("msg_type")
  private int msgType;
  @JsonProperty("operation")
  private int opType;
  @JsonProperty("num_records")
  private long numRecords;
  private Scene[] scenes;
  @JsonProperty("err_code")
  private int errorCode;
  @JsonProperty("err_msg")
  private String errorMessage;
  @JsonProperty("transaction_id")
  private String transactionId;

  /**
  * Default empty SceneList constructor.
  */
  public SceneList() {
    super();
    this.msgType = -1;
    this.numRecords = 1;
    this.scenes = new Scene[0];
    this.errorCode = 100;
    this.errorMessage = "";
    this.transactionId = "";
    this.opType = 10;
  }

  /**
  * SceneList constructor with message type and scene list.
  */
  public SceneList(int newMsgType, Scene[] newSceneList) {
    super();
    this.msgType = newMsgType;
    this.opType = 10;
    this.numRecords = 1;
    this.scenes = newSceneList;
    this.errorCode = 100;
    this.errorMessage = "";
    this.transactionId = "";
  }

  /**
  * Complete SceneList constructor.
  * @param msgType Integer Value representing the Type of Message.
  * @param numRecords The Number of Records in the Scene List.
  * @param scenes The list of Scenes stored in the Scene List.
  * @param errCode The Integer Error Code for the message.
  * @param errMsg The Human-Readable Error Code in the message.
  * @param transactionId The Unique Identifier for a particular transaction
  */
  public SceneList(int msgType, long numRecords, Scene[] scenes,
      int errCode, String errMsg, String transactionId) {
    this.msgType = msgType;
    this.numRecords = numRecords;
    this.scenes = scenes;
    this.errorCode = errCode;
    this.errorMessage = errMsg;
    this.transactionId = transactionId;
  }

  /**
  * Returns value of msgType.
  * @return Integer Value representing the Type of Message.
  */
  @JsonGetter("msg_type")
  public int getMsgType() {
    return this.msgType;
  }

  /**
  * Returns value of opType.
  * @return Integer Value representing the Type of operation.
  */
  @JsonGetter("operation")
  public int getOpType() {
    return this.opType;
  }

  /**
  * Set value of msgType.
  * @param newMsgType Integer Value representing the Type of Message.
  */
  @JsonSetter("msg_type")
  public void setMsgType(int newMsgType) {
    this.msgType = newMsgType;
  }

  /**
  * Set value of msgType.
  * @param newOpType Integer Value representing the Type of Message.
  */
  @JsonSetter("operation")
  public void setOpType(int newOpType) {
    this.opType = newOpType;
  }

  /**
  * Returns value of numRecords.
  * @return The Number of Records in the Scene List.
  */
  @JsonGetter("num_records")
  public long getNumRecords() {
    return this.numRecords;
  }

  /**
  * Set value of numRecords.
  * @param newNumRecords The Number of Records in the Scene List.
  */
  @JsonSetter("num_records")
  public void setNumRecords(long newNumRecords) {
    this.numRecords = newNumRecords;
  }

  /**
  * Returns the Scene list.
  * @return The list of Scenes stored in the Scene List.
  */
  @JsonGetter("scenes")
  public Scene[] getSceneList() {
    return this.scenes;
  }

  /**
  * Set the Scene list.
  * @param newSceneList The list of Scenes stored in the Scene List.
  */
  @JsonSetter("scenes")
  public void setSceneList(Scene[] newSceneList) {
    this.scenes = newSceneList;
  }

  /**
  * Returns value of errorCode.
  * @return The Integer Error Code for the message.
  */
  @JsonGetter("err_code")
  public int getErrorCode() {
    return this.errorCode;
  }

  /**
  * Set value of errorCode.
  * @param newErrorCode The Integer Error Code for the message.
  */
  @JsonSetter("err_code")
  public void setErrorCode(int newErrorCode) {
    this.errorCode = newErrorCode;
  }

  /**
  * Returns value of errorMessage.
  * @return The Human-Readable Error Code in the message.
  */
  @JsonGetter("err_msg")
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
  * Set value of errorMessage.
  * @param newErrorMessage The Human-Readable Error Code in the message.
  */
  @JsonSetter("err_msg")
  public void setErrorMessage(String newErrorMessage) {
    this.errorMessage = newErrorMessage;
  }

  /**
  * Returns value of errorMessage.
  * @return The Unique Identifier for a particular transaction.
  */
  @JsonGetter("transaction_id")
  public String getTransactionId() {
    return this.transactionId;
  }

  /**
  * Set value of errorMessage.
  * @param newTransactionId The Unique Identifier for a particular transaction.
  */
  @JsonSetter("transaction_id")
  public void setTransactionId(String newTransactionId) {
    this.transactionId = newTransactionId;
  }
}
