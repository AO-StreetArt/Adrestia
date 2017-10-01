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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.zeromq.ZContext;
import org.zeromq.ZPoller;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.cache.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
* A Manager for Dvs Service Instances
* Serves up instances of CLyman, Crazy Ivan, and Ceph
*/
@Component
public class DvsManager {
  // List of Blacklisted services
  // Consul Client for executing Service Discovery
  @Autowired
  org.springframework.cloud.client.discovery.DiscoveryClient consul_client;

  // Crazy Ivan Service Instance
  private static org.springframework.cloud.client.ServiceInstance crazyIvanInstance;

  // CLyman Service Instance
  private static org.springframework.cloud.client.ServiceInstance clymanInstance;

  // ZMQ Context
  public static final ZContext context = new ZContext();

  // Crazy Ivan ZMQ Socket
  private static ZMQ.Socket crazyIvanSocket = null;

  // CLyman ZMQ Socket
  private static ZMQ.Socket clymanSocket = null;

  // ZMQ Polle used to pull messages to and from ZMQ.
  ZPoller poller = new ZPoller(context);

  // Mutex to ensure that one thread is accessing the ZMQ Socket at a time
  private static Semaphore crazyIvanMutex = new Semaphore(1);

  // Mutex to ensure that one thread is accessing the ZMQ Socket at a time
  private static Semaphore clymanMutex = new Semaphore(1);

  // DVS Manager Logger
  private static final Logger logger = LogManager.getLogger("adrestia.DvsManager");

  // Loading cache to hold blacklisted hosts
  // Keys will expire after 5 minutes, at which point Consul should be able
  // to determine if the service is active or inactive.
  LoadingCache<String, Object> ivan_host_blacklist = CacheBuilder.newBuilder()
  .expireAfterAccess(300, TimeUnit.SECONDS)
  .maximumSize(50)
  .weakKeys()
  .build(new CacheLoader<String, Object>() {
    @Override
    public Object load(String key) throws Exception {
      return "new-value-loaded-" + key;
    }
  });

  // Loading Cache to hold greylisted hosts
  // Keys will expire after 30 seconds, at which point we try again
  LoadingCache<String, Object> ivan_host_greylist = CacheBuilder.newBuilder()
  .expireAfterAccess(30, TimeUnit.SECONDS)
  .maximumSize(30)
  .weakKeys()
  .build(new CacheLoader<String, Object>() {
    @Override
    public Object load(String key) throws Exception {
      return "new-value-loaded-" + key;
    }
  });

  /**
  * Default empty DvsManager constructor
  */
  public DvsManager() {
    super();
  }

  // Destroy the Scene Controller
  @PreDestroy
  public void destroy() {
    if (crazyIvanSocket != null) {
      context.destroySocket(crazyIvanSocket);
    }
    if (clymanSocket != null) {
      context.destroySocket(crazyIvanSocket);
    }
    context.destroy();
  }

  private boolean ivan_socket_active() {
    if (crazyIvanSocket != null) {
      return true;
    }
    return false;
  }

  // Destroy the active Crazy Ivan Socket
  private void destroy_ivan_socket() {
    if (crazyIvanSocket != null) {
      poller.unregister(crazyIvanSocket);
      context.destroySocket(crazyIvanSocket);
      crazyIvanSocket = null;
    }
  }

  // Reset the active Crazy Ivan Socket
  private void reset_ivan_socket() {
    destroy_ivan_socket();
    crazyIvanSocket = context.createSocket(ZMQ.REQ);
    poller.register(crazyIvanSocket, ZPoller.POLLIN);
  }

  // Connect to the current Crazy Ivan Instance
  private void connect_to_ivan_socket() {
    String crazyIvanUriString = crazyIvanInstance.getUri().toString();
    int port_seperator_index = crazyIvanUriString.lastIndexOf(":");
    String crazyIvanHostName = crazyIvanUriString.substring(7, port_seperator_index);
    String crazyIvanPortStr = crazyIvanUriString.substring(port_seperator_index + 1, crazyIvanUriString.length());
    String zmq_addr = String.format("tcp://%s:%s", crazyIvanHostName, crazyIvanPortStr);
    logger.info("Connecting to server: " + zmq_addr);
    crazyIvanSocket.connect(zmq_addr);
  }

  private void report_ivan_failure() {
    // Is the current host already on the greylist?
    Object cache_resp = ivan_host_greylist.getIfPresent(crazyIvanInstance.getUri().toString());
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      // Eliminate the socket
      destroy_ivan_socket();
      if (cache_resp != null) {
        // We have found an entry in the greylist, add the host to the blacklist
        ivan_host_blacklist.get(crazyIvanInstance.getUri().toString());
      } else {
        // We have no entry in the greylist, add the hostname to the greylist
        ivan_host_greylist.get(crazyIvanInstance.getUri().toString());
      }
    } catch (Exception e) {
      logger.error("Error Resetting Crazy Ivan connection");
      logger.error(e.getMessage());
    }
  }

  // Setup method to find and connect to an instance of Crazy Ivan
  private void find_ivan() {
    logger.info("Finding a new Crazy Ivan instance");
    // Find an instance of CrazyIvan
    List<org.springframework.cloud.client.ServiceInstance> serviceInstances =
                                            consul_client.getInstances("Ivan");
    if (serviceInstances != null ) {
      //Log if we find no service instances
      if (serviceInstances.size() == 0) {
        logger.error("No Crazy Ivan instances found");
      }
      // Find a service Instance not on the blacklist
      for (int i = 0; i < serviceInstances.size(); i++) {
        crazyIvanInstance = serviceInstances.get(i);
        logger.debug("Found Crazy Ivan Instance: " + crazyIvanInstance.getUri().toString());
        Object cache_resp = ivan_host_blacklist.getIfPresent(crazyIvanInstance.getUri().toString());
        if (cache_resp == null) {
          try {
            // Crazy Ivan ZMQ Context & Socket
            // Close any existing socket before creating a new one
            reset_ivan_socket();

            // Connect to the new socket
            // First we need to format the address from Consul.  We also assume tcp
            // Communications between this class and Crazy Ivan
            connect_to_ivan_socket();
          } catch (Exception e) {
            logger.error("Error connecting to Crazy Ivan instance");
            logger.error(e.getMessage());
            report_ivan_failure();
          }
          // Exit the loop
          break;
        } else {
          logger.error("Returned host found in blacklist");
          crazyIvanInstance = null;
        }
      }
    } else {
      logger.error("Unable to find Crazy Ivan instance");
    }
  }

  private String send_to_ivan_recursive(String msg, int timeout, int retries) {
    logger.info("Attempting to send message to Crazy Ivan");
    // Find a Crazy Ivan instance, if necessary
    if (crazyIvanSocket == null) {
      find_ivan();
    }
    // Response Processing
    int retriesLeft = retries;
    while (retriesLeft > 0 && !Thread.currentThread().isInterrupted() && crazyIvanSocket != null) {
      //  We send a request, then we work to get a reply
      crazyIvanSocket.send(msg.getBytes(ZMQ.CHARSET), 0);

      // We are going to use a poller with a timeout to get the value
      // Pattern from ZMQ Guide - Lazy Pirate Client
      // http://zguide.zeromq.org/java:lpclient
      int expect_reply = 1;
      while (expect_reply > 0) {
        //  Poll socket for a reply, with timeout
        int rc = poller.poll(timeout);
        logger.debug("Poller Checked with Result Code:");
        logger.debug(rc);
        //  Here we process a server reply and exit our loop if the
        //  reply is valid. If we didn't get a reply we close the client
        //  socket and resend the request. We try a number of times
        //  before finally abandoning:
        if (poller.isReadable(crazyIvanSocket)) {
          //  We got a reply from the server
          return crazyIvanSocket.recvStr();
        } else if (--retriesLeft == 0) {
          logger.error("Reporting Crazy Ivan Failure");
          report_ivan_failure();
          // Keep trying to send the message until we succeed or run out of
          // Crazy Ivan instances
          return send_to_ivan_recursive(msg, timeout, retries);
        } else {
          logger.warn("No response from server, retrying");
          //  Old socket is confused; close it and open a new one
          reset_ivan_socket();
          connect_to_ivan_socket();
          //  Send request again, on new socket
          crazyIvanSocket.send(msg.getBytes(ZMQ.CHARSET), 0);
        }
        if (rc < 0) {break;}
      }
    }
    return null;
  }

  // Send a message to Crazy Ivan, return the response
  public String send_to_ivan(String msg, int timeout, int retries) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      crazyIvanMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock on ZMQ Socket");
      logger.error(e.getMessage());
    }
    // Actually try to send the message
    try {
      return send_to_ivan_recursive(msg, timeout, retries);
    } catch (Exception e) {
      logger.error("Error Sending message to Crazy Ivan: ", e);
    } finally {
      // Release the mutex
      crazyIvanMutex.release();
    }
    return null;
  }

}
