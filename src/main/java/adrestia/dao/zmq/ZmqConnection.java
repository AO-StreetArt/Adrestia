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

package adrestia.dao.zmq;

import org.springframework.stereotype.Service;

/**
* A Service which connects to an external ZMQ Service.
*/
@Service
public interface ZmqConnection {

  /**
  * Send a message to the ZMQ Socket, return the response.
  * Send and return a string
  * @param msg The String message to send on the ZMQ Socket
  * @param timeout How many milliseconds to wait before retrying
  * @param retries How many times to retry before reporting a failure
  * @param serviceName The Name of the Service we are sending to, in Consul
  * @return The String response message from a matching service
  */
  public String send(String msg, int timeout, int retries, String serviceName);
}
