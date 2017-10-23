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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ;

/**
* A Pool of ZMQ Sockets
*/
@Component
public class ZmqSocketPool {
  // ZMQ Context
  @Autowired
  ZmqContextContainer context;

  // List of available ZmqSocketContainers
  private List sockets = new ArrayList();

  // Socket ID Counter
  private int idCounter = 0;

  // List of currently used Socket Containers
  private List usedSockets = new ArrayList();

  // Crazy Ivan ZMQ Socket
  private ZMQ.Socket socket = null;

  // Mutex to ensure that one thread is updating the socket list at a time
  private Semaphore socketMutex = new Semaphore(1);

  // ZMQ Socket Pool Logger
  private final Logger logger = LogManager.getLogger("adrestia.ZmqSocketPool");

  /**
  * Default empty ZmqSocketPool constructor.
  */
  public ZmqSocketPool() {
    super();
  }

  /**
  * Is a socket in use already?
  * @return True if the socket is already in use, false otherwise
  */
  public boolean socketInUse(String connection) {
    for (int i = 0; i < usedSockets.size(); i++) {
      if (usedSockets.get(i).getHostname().equals(connection)) {
        return true;
      }
    }
    return false;
  }

  /**
  * Is a socket already available for the given host?
  * @return True if the socket is already available, false otherwise
  */
  public boolean socketAvailable(String connection) {
    for (int i = 0; i < sockets.size(); i++) {
      if (sockets.get(i).getHostname().equals(connection)) {
        return true;
      }
    }
    return false;
  }

  /**
  * Get a socket, creating it if necessary
  * @return A Socket container for use by a thread
  */
  public ZmqSocketContainer getSocket(String connection, int serviceType) {
    try {
      socketMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock");
      logger.error(e.getMessage());
      return null;
    }

    // Build a new socket
    ZmqSocketContainer newSocket = null;
    try {
      boolean makeNewSocket = true;
      // Check if we have any available sockets
      for (int i = 0; i < sockets.size(); i++) {
        if (sockets.get(i).getHostname().equals(connection)) {
          newSocket = sockets.get(i).getSocket();
          makeNewSocket = false;
        }
      }

      // Check if we have any sockets currently in use
      for (int i = 0; i < usedSockets.size(); i++) {
        if (usedSockets.get(i).getHostname().equals(connection)) {
          makeNewSocket = false;
        }
      }

      // Build a new socket
      if (makeNewSocket) {
        newSocket = new ZmqSocketContainer(idCounter, serviceType, connection, context.context.createSocket(ZMQ.REQ));
        usedSockets.add(newSocket);
        idCounter++;
      }
    } catch (Exception e) {
      logger.error("Error Getting Socket: ", e);
    } finally {
      // Release the mutex
      socketMutex.release();
    }
    return newSocket;
  }

  /**
  * Release a socket for use by another thread
  * @param cont The Socket Container to release
  */
  public void releaseSocket(ZmqSocketContainer cont) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      socketMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock");
      logger.error(e.getMessage());
      return "";
    }
    try {
      for (int i = 0; i < usedSockets.size(); i++) {
        if (usedSockets.get(i).getId() == cont.getId()) {
          usedSockets.remove(i);
          sockets.add(usedSockets.get(i));
        }
      }
    } catch (Exception e) {
      logger.error("Error Releasing Socket: ", e);
    } finally {
      // Release the mutex
      socketMutex.release();
    }
  }

  /**
  * Close a failed socket
  * @param cont The Socket Container to close
  */
  public void closeSocket(SocketContainer cont) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      socketMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock");
      logger.error(e.getMessage());
      return "";
    }
    try {
      context.context.destroySocket(sockets.get(i).getSocket());
      for (int i = 0; i < usedSockets.size(); i++) {
        if (usedSockets.get(i).getId() == cont.getId()) {
          usedSockets.remove(i);
        }
      }
      for (int i = 0; i < sockets.size(); i++) {
        if (sockets.get(i).getId() == cont.getId()) {
          sockets.remove(i);
        }
      }
    } catch (Exception e) {
      logger.error("Error Closing Socket: ", e);
    } finally {
      // Release the mutex
      socketMutex.release();
    }
  }
}
