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
* A Pool of ZMQ Sockets.
*/
@Component
public class ZmqSocketPool {
  // ZMQ Context
  @Autowired
  ZmqContextContainer context;

  // List of available ZmqSocketContainers
  private ArrayList<ZmqSocketContainer> sockets = new ArrayList<ZmqSocketContainer>();

  // Socket ID Counter
  private int idCounter = 0;

  // List of currently used Socket Containers
  private ArrayList<ZmqSocketContainer> usedSockets = new ArrayList<ZmqSocketContainer>();

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
  * Destroy the ZMQ Socket Pool, closing any open sockets.
  */
  @PreDestroy
  public void destroy() {
    // Destroy any active sockets
    for (int i = 0; i < sockets.size(); i++) {
      sockets.remove(i);
      context.context.destroySocket(sockets.get(i).getSocket());
    }
  }

  /**
  * Reset a Socket.
  * @return A New Socket Container
  */
  public ZmqSocketContainer resetSocket(ZmqSocketContainer connection) {
    context.context.destroySocket(connection.getSocket());
    ZmqSocketContainer newSocket = new ZmqSocketContainer(
        connection.getId(), connection.getServiceType(),
        connection.getHostname(), context.context.createSocket(ZMQ.REQ));
    newSocket.getSocket().connect(connection.getHostname());
    return newSocket;
  }

  /**
  * Is a socket in use already.
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
  * Is a socket already available for the given host.
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
  * Get a socket, creating it if necessary.
  * @return A Socket container for use by a thread
  */
  public ZmqSocketContainer getSocket(String connection, int serviceType) {
    logger.info("Getting Socket: " + connection);
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
          newSocket = sockets.get(i);
          logger.debug("Identified existing socket: " + sockets.get(i).getHostname());
          makeNewSocket = false;
        }
      }

      // Check if we have any sockets currently in use
      // Pretty sure we still need a new socket here (multiple threads)
      // for (int i = 0; i < usedSockets.size(); i++) {
      //   if (usedSockets.get(i).getHostname().equals(connection)) {
      //     makeNewSocket = false;
      //     logger.debug("Identified Socket in use: " + usedSockets.get(i).getHostname());
      //   }
      // }

      // Build a new socket
      if (makeNewSocket) {
        logger.debug("Establishing new ZMQ Connection" + connection);
        newSocket = new ZmqSocketContainer(
            idCounter, serviceType, connection, context.context.createSocket(ZMQ.REQ));
        newSocket.getSocket().connect(connection);
        usedSockets.add(newSocket);
        idCounter++;
      }
    } catch (Exception e) {
      logger.error("Error Getting Socket: ", e);
      newSocket = null;
    } finally {
      // Release the mutex
      socketMutex.release();
    }
    return newSocket;
  }

  /**
  * Release a socket for use by another thread.
  * @param cont The Socket Container to release
  */
  public void releaseSocket(ZmqSocketContainer cont) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      socketMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock");
      logger.error(e.getMessage());
      return;
    }
    try {
      for (int i = 0; i < usedSockets.size(); i++) {
        if (usedSockets.get(i).getId() == cont.getId()) {
          sockets.add(usedSockets.get(i));
          usedSockets.remove(i);
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
  * Close a failed socket, must be released first.
  * @param cont The Socket Container to close
  */
  public void closeSocket(ZmqSocketContainer cont) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      socketMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock");
      logger.error(e.getMessage());
      return;
    }
    try {
      for (int i = 0; i < usedSockets.size(); i++) {
        if (usedSockets.get(i).getId() == cont.getId()) {
          usedSockets.remove(i);
        }
      }
      for (int i = 0; i < sockets.size(); i++) {
        if (sockets.get(i).getId() == cont.getId()) {
          sockets.remove(i);
          context.context.destroySocket(sockets.get(i).getSocket());
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
