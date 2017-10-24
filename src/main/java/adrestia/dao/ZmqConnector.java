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

  // ZMQ Context
  @Autowired
  ZmqContextContainer context;

  // ZMQ Socket Pool
  @Autowired
  ZmqSocketPool socketPool;

  // Crazy Ivan ZMQ Socket
  private ZMQ.Socket socket = null;

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

  private String getZmqAddr(ServiceInstance service) {
    // Pull the URL String
    String uriString = service.getUri().toString();
    // Parse the URL String
    int portSeperatorIndex = uriString.lastIndexOf(":");
    String hostName = uriString.substring(7, portSeperatorIndex);
    String portStr =
        uriString.substring(portSeperatorIndex + 1, uriString.length());
    return String.format("tcp://%s:%s", hostName, portStr);
  }

  // TO-DO: Accept the failed service as an input
  // Report a failure of a service
  private void reportFailure(ServiceInstance connectedInstance) {
    // Is the current host already on the greylist?
    Object cacheResp =
        greylist.getIfPresent(connectedInstance.getUri().toString());
    try {
      if (cacheResp != null) {
        // We have found an entry in the greylist, add the host to the blacklist
        cacheResp = blacklist.get(connectedInstance.getUri().toString());
      } else {
        // We have no entry in the greylist, add the hostname to the greylist
        cacheResp = greylist.get(connectedInstance.getUri().toString());
      }
    } catch (Exception e) {
      logger.error("Error Reporting failure");
      logger.error(e.getMessage());
    }
  }

  // Setup method to find and connect to an instance of a specified service name
  private ZmqSocketContainer findService(String serviceName) {
    int serviceType = -1;
    if (serviceName.equals("Ivan")) {
      serviceType = ZmqSocketContainer.ivanType;
    } else if (serviceName.equals("Clyman")) {
      serviceType = ZmqSocketContainer.clymanType;
    }
    logger.info("Finding a new Service instance");
    ZmqSocketContainer transactionSocket = null;
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
        ServiceInstance connectedInstance = serviceInstances.get(i);
        logger.debug("Found Service Instance: "
            + connectedInstance.getUri().toString());
        Object cacheResp =
            blacklist.getIfPresent(connectedInstance.getUri().toString());
        // We can go ahead and connect to the instance as long as it isn't
        // on the blacklist
        if (cacheResp == null) {
          try {
            // Get a socket from the socket pool
            transactionSocket = socketPool.getSocket(getZmqAddr(connectedInstance), serviceType);
          } catch (Exception e) {
            logger.error("Error connecting to Crazy Ivan instance");
            logger.error(e.getMessage());
            reportFailure(connectedInstance);
          }
          // Exit the loop
          transactionSocket.setService(connectedInstance);
          return transactionSocket;
        } else {
          logger.error("Returned host found in blacklist");
          connectedInstance = null;
        }
      }
    } else {
      logger.error("Unable to find Service instance");
    }
    return null;
  }

  private String sendMsgWithRetry(String msg, int timeout,
      int retries, String serviceName, ZmqSocketContainer socketContainer) {
    logger.info("Attempting to send message to ZMQ Service");
    // ZMQ Poller used to pull messages from ZMQ.
    ZPoller poller = new ZPoller(context.context);
    ZMQ.Socket socket = socketContainer.getSocket();
    poller.register(socket, ZPoller.POLLIN);
    // Response Processing
    int retriesLeft = retries;
    while (retriesLeft > 0 && !Thread.currentThread().isInterrupted()) {
      //  We send a request, then we work to get a reply
      socket.send(msg.getBytes(ZMQ.CHARSET), 0);

      // We are going to use a poller with a timeout to get the value
      // Pattern from ZMQ Guide - Lazy Pirate Client
      // http://zguide.zeromq.org/java:lpclient
      int expectReply = 1;
      while (expectReply > 0) {
        //  Poll socket for a reply, with timeout
        expectReply = poller.poll(timeout);
        logger.debug("Poller Checked with Result Code:");
        logger.debug(expectReply);
        //  Here we process a server reply and exit our loop if the
        //  reply is valid. If we didn't get a reply we close the client
        //  socket and resend the request. We try a number of times
        //  before finally abandoning:
        if (poller.isReadable(socket)) {
          //  We got a reply from the server
          return socket.recvStr();
        } else if (--retriesLeft == 0) {
          logger.error("Reporting Crazy Ivan Failure");
          reportFailure(socketContainer.getService());
          return null;
        } else {
          logger.warn("No response from server, retrying");
          //  Old socket is confused; close it and open a new one
          socketContainer = socketPool.resetSocket(socketContainer);
          socket = socketContainer.getSocket();
          poller.register(socket, ZPoller.POLLIN);
          //  Send request again, on new socket
          socket.send(msg.getBytes(ZMQ.CHARSET), 0);
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
    // Find a ZMQ Socket
    ZmqSocketContainer transactionSocket = findService(serviceName);
    if (transactionSocket != null) {
      ZMQ.Socket socket = transactionSocket.getSocket();
    } else {
      return null;
    }
    // Actually try to send the message
    String response = null;
    try {
      response = sendMsgWithRetry(msg, timeout, retries, serviceName, transactionSocket);
      // Keep trying to send the message until we succeed or run out of
      // Crazy Ivan instances
      while ((response == null) && (transactionSocket != null)) {
        socketPool.releaseSocket(transactionSocket);
        socketPool.closeSocket(transactionSocket);
        transactionSocket = findService(serviceName);
        response = sendMsgWithRetry(msg, timeout, retries, serviceName, transactionSocket);
      }
    } catch (Exception e) {
      logger.error("Error Sending message to Service: ", e);
    } finally {
      // Ensure we release the socket from the pool
      socketPool.releaseSocket(transactionSocket);
    }
    return response;
  }
}
