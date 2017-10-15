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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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

import org.zeromq.ZContext;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ;
import org.zeromq.ZPoller;

/**
* A Service which connects to an external ZMQ Service.
*/
public class ZmqConnector {

  // Consul Client for executing Service Discovery
  @Autowired
  DiscoveryClient consulClient;

  // The Currently Connected Service Instance
  private static ServiceInstance connectedInstance;

  // ZMQ Context
  @Autowired
  ZmqContextContainer context;

  // Crazy Ivan ZMQ Socket
  private ZMQ.Socket socket = null;

  // ZMQ Poller used to pull messages from ZMQ.
  private ZPoller poller = new ZPoller(context.context);

  // Mutex to ensure that one thread is accessing the ZMQ Socket at a time
  private Semaphore socketMutex = new Semaphore(1);

  // ZMQ Connector Logger
  private final Logger logger = LogManager.getLogger("adrestia.ZMQ");

  // Loading cache to hold blacklisted CLyman hosts
  // Keys will expire after 5 minutes, at which point Consul should be able
  // to determine if the service is active or inactive.
  LoadingCache<String, Object> blacklist = CacheBuilder.newBuilder()
      .expireAfterAccess(300, TimeUnit.SECONDS)
      .maximumSize(50)
      .weakKeys()
      .build(new CacheLoader<String, Object>() {
        @Override
        public Object load(String key) throws Exception {
          return key;
        }
      });

  // Loading Cache to hold greylisted CLyman hosts
  // Keys will expire after 30 seconds, if we report another failure in this
  // time then the service will be blacklisted
  LoadingCache<String, Object> greylist = CacheBuilder.newBuilder()
      .expireAfterAccess(30, TimeUnit.SECONDS)
      .maximumSize(30)
      .weakKeys()
      .build(new CacheLoader<String, Object>() {
        @Override
        public Object load(String key) throws Exception {
          return key;
        }
      });

  /**
  * Default empty DvsManager constructor.
  */
  public ZmqConnector() {
    super();
  }

  // Check if a service is active
  private boolean isSocketActive() {
    if (socket != null) {
      return true;
    }
    return false;
  }

  // Destroy the active socket for a service
  private void destroySocket() {
    if (isSocketActive()) {
      poller.unregister(socket);
      context.context.destroySocket(socket);
      socket = null;
    }
  }

  // Reset the active socket for a service
  private void resetSocket() {
    // First, destroy the socket
    destroySocket();
    // Then, create a new socket
    socket = context.context.createSocket(ZMQ.REQ);
    poller.register(socket, ZPoller.POLLIN);
  }

  /**
  * Destroy the ZMQ Connector, closing any open sockets.
  */
  @PreDestroy
  public void destroy() {
    destroySocket();
  }

  // Connect to the current socket for a service
  private void connectToSocket() {
    // Pull the URL String
    String uriString = connectedInstance.getUri().toString();
    // Parse the URL String
    int portSeperatorIndex = uriString.lastIndexOf(":");
    String hostName = uriString.substring(7, portSeperatorIndex);
    String portStr =
        uriString.substring(portSeperatorIndex + 1, uriString.length());
    String zmqAddr = String.format("tcp://%s:%s", hostName, portStr);
    logger.info("Connecting to server: " + zmqAddr);
    // Connect to the service
    socket.connect(zmqAddr);
  }

  // Report a failure of a service
  private void reportFailure() {
    // Is the current host already on the greylist?
    Object cacheResp =
        greylist.getIfPresent(connectedInstance.getUri().toString());
    try {
      // Eliminate the socket
      destroySocket();
      if (cacheResp != null) {
        // We have found an entry in the greylist, add the host to the blacklist
        cacheResp = blacklist.get(connectedInstance.getUri().toString());
      } else {
        // We have no entry in the greylist, add the hostname to the greylist
        cacheResp = greylist.get(connectedInstance.getUri().toString());
      }
    } catch (Exception e) {
      logger.error("Error Resetting Crazy Ivan connection");
      logger.error(e.getMessage());
    }
  }

  // Setup method to find and connect to an instance of a specified service name
  private void findService(String serviceName) {
    logger.info("Finding a new Service instance");
    // Find an instance of CrazyIvan
    List<ServiceInstance> serviceInstances =
        consulClient.getInstances(serviceName);
    if (serviceInstances != null) {
      //Log if we find no service instances
      if (serviceInstances.size() == 0) {
        logger.error("No Service instances found");
      }
      // Find a service Instance not on the blacklist
      for (int i = 0; i < serviceInstances.size(); i++) {
        // Pull the service instance, and the value from the blacklist
        connectedInstance = serviceInstances.get(i);
        logger.debug("Found Service Instance: "
            + connectedInstance.getUri().toString());
        Object cacheResp =
            blacklist.getIfPresent(connectedInstance.getUri().toString());
        // We can go ahead and connect to the instance as long as it isn't
        // on the blacklist
        if (cacheResp == null) {
          try {
            // Crazy Ivan ZMQ Context & Socket
            // Close any existing socket before creating a new one
            resetSocket();

            // Connect to the new socket
            // First we need to format the address from Consul.  We also assume
            // tcp Communications between this class and Crazy Ivan
            connectToSocket();
          } catch (Exception e) {
            logger.error("Error connecting to Crazy Ivan instance");
            logger.error(e.getMessage());
            reportFailure();
          }
          // Exit the loop
          break;
        } else {
          logger.error("Returned host found in blacklist");
          connectedInstance = null;
        }
      }
    } else {
      logger.error("Unable to find Service instance");
    }
  }

  private String sendMsgRecursive(
      String msg, int timeout, int retries, String serviceName) {
    logger.info("Attempting to send message to ZMQ Service");
    // Find a Service instance, if necessary
    if (!isSocketActive()) {
      findService(serviceName);
    }
    // Response Processing
    int retriesLeft = retries;
    while (retriesLeft > 0
        && !Thread.currentThread().isInterrupted()
        && isSocketActive()) {
      //  We send a request, then we work to get a reply
      socket.send(msg.getBytes(ZMQ.CHARSET), 0);

      // We are going to use a poller with a timeout to get the value
      // Pattern from ZMQ Guide - Lazy Pirate Client
      // http://zguide.zeromq.org/java:lpclient
      int expectReply = 1;
      while (expectReply > 0) {
        //  Poll socket for a reply, with timeout
        int rc = poller.poll(timeout);
        logger.debug("Poller Checked with Result Code:");
        logger.debug(rc);
        //  Here we process a server reply and exit our loop if the
        //  reply is valid. If we didn't get a reply we close the client
        //  socket and resend the request. We try a number of times
        //  before finally abandoning:
        if (poller.isReadable(socket)) {
          //  We got a reply from the server
          return socket.recvStr();
        } else if (--retriesLeft == 0) {
          logger.error("Reporting Crazy Ivan Failure");
          reportFailure();
          // Keep trying to send the message until we succeed or run out of
          // Crazy Ivan instances
          return sendMsgRecursive(msg, timeout, retries, serviceName);
        } else {
          logger.warn("No response from server, retrying");
          //  Old socket is confused; close it and open a new one
          resetSocket();
          connectToSocket();
          //  Send request again, on new socket
          socket.send(msg.getBytes(ZMQ.CHARSET), 0);
        }
        if (rc < 0) {
          break;
        }
      }
    }
    return null;
  }

  /**
  * Send a message to the ZMQ Socket, return the response.
  * Send and return a string
  * @param msg The String message to send on the ZMQ Socket
  * @param timeout How many milliseconds to wait before retrying
  * @param retries How many times to retry before reporting a failure
  * @param serviceName The Name of the Service we are sending to, in Consul
  * @return The String response message from a matching service
  */
  public String send(String msg, int timeout, int retries, String serviceName) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      socketMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock on ZMQ Socket");
      logger.error(e.getMessage());
      return "";
    }
    // Actually try to send the message
    try {
      return sendMsgRecursive(msg, timeout, retries, serviceName);
    } catch (Exception e) {
      logger.error("Error Sending message to Service: ", e);
    } finally {
      // Release the mutex
      socketMutex.release();
    }
    return null;
  }
}
