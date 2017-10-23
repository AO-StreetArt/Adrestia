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


import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ;

/**
* A Container for a single ZMQ Socket
*/
public class ZmqSocketContainer {
  private static final int ivanType = 0;
  private static final int clymanType = 1;
  private int id = null;
  private int serviceType = null;
  private String hostname = null;
  private ZMQ.Socket socket = null;

	/**
	* Default empty ZmqSocketContainer constructor
	*/
	public ZmqSocketContainer() {
		super();
	}

	/**
	* Default ZmqSocketContainer constructor
	*/
	public ZmqSocketContainer(int id, int serviceType, String hostname, ZMQ.Socket sock) {
		super();
		this.id = id;
		this.serviceType = serviceType;
		this.hostname = hostname;
		this.socket = sock;
	}

	/**
	* Returns value of id
	* @return The internal ID of the ZMQ Socket
	*/
	public int getId() {
		return this.id;
	}

	/**
	* Returns value of serviceType
	* @return An integer code for the service type
	*/
	public int getServiceType() {
		return this.serviceType;
	}

	/**
	* Returns value of hostname
	* @return The hostname of the socket
	*/
	public String getHostname() {
		return this.hostname;
	}

	/**
	* Returns the ZMQ Socket
	* @return The ZMQ Socket
	*/
	public ZMQ.Socket getSocket() {
		return this.socket;
	}
}
