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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.zeromq.ZContext;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ;
import org.zeromq.ZPoller;

/**
* A Manager for Dvs Service Instances.
* Serves up instances of CLyman, Crazy Ivan, and Ceph.
*/
@Component
public class DvsManager {
  // Constant integers so that we can unify internal method calls for
  // CLyman and CrazyIvan
  private static int clymanType = 0;
  private static int ivanType = 1;
  // Consul Client for executing Service Discovery
  @Autowired
  DiscoveryClient consulClient;

  // Crazy Ivan Service Instance
  private static ServiceInstance crazyIvanInstance;

  // CLyman Service Instance
  private static ServiceInstance clymanInstance;

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
  private static final Logger logger =
      LogManager.getLogger("adrestia.DvsManager");

  // Loading cache to hold blacklisted CLyman hosts
  // Keys will expire after 5 minutes, at which point Consul should be able
  // to determine if the service is active or inactive.
  LoadingCache<String, Object> clymanBlacklist = CacheBuilder.newBuilder()
      .expireAfterAccess(300, TimeUnit.SECONDS)
      .maximumSize(50)
      .weakKeys()
      .build(new CacheLoader<String, Object>() {
        @Override
        public Object load(String key) throws Exception {
          return "new-value-loaded-" + key;
        }
      });

  // Loading Cache to hold greylisted CLyman hosts
  // Keys will expire after 30 seconds, if we report another failure in this
  // time then the service will be blacklisted
  LoadingCache<String, Object> clymanGreylist = CacheBuilder.newBuilder()
      .expireAfterAccess(30, TimeUnit.SECONDS)
      .maximumSize(30)
      .weakKeys()
      .build(new CacheLoader<String, Object>() {
        @Override
        public Object load(String key) throws Exception {
          return "new-value-loaded-" + key;
        }
      });

  // Loading cache to hold blacklisted CrazyIvan hosts
  // Keys will expire after 5 minutes, at which point Consul should be able
  // to determine if the service is active or inactive.
  LoadingCache<String, Object> ivanBlacklist = CacheBuilder.newBuilder()
      .expireAfterAccess(300, TimeUnit.SECONDS)
      .maximumSize(50)
      .weakKeys()
      .build(new CacheLoader<String, Object>() {
        @Override
        public Object load(String key) throws Exception {
          return "new-value-loaded-" + key;
        }
      });

  // Loading Cache to hold greylisted CrazyIvan hosts
  // Keys will expire after 30 seconds, if we report another failure in this
  // time then the service will be blacklisted
  LoadingCache<String, Object> ivanGreylist = CacheBuilder.newBuilder()
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
  * Default empty DvsManager constructor.
  */
  public DvsManager() {
    super();
  }

  // Check if a service is active
  private static boolean is_socket_active(int serviceType) {
    if (serviceType == ivanType) {
      if (crazyIvanSocket != null) {
        return true;
      }
      return false;
    } else if (serviceType == clymanType) {
      if (clymanSocket != null) {
        return true;
      }
      return false;
    }
    return false;
  }

  // Get the active socket for a service
  private static ZMQ.Socket get_socket(int serviceType) {
    if (serviceType == ivanType) {
      return crazyIvanSocket;
    } else if (serviceType == clymanType) {
      return clymanSocket;
    }
    return null;
  }

  // Destroy the active socket for a service
  private void destroy_socket(int serviceType) {
    if (serviceType == ivanType) {
      if (crazyIvanSocket != null) {
        poller.unregister(crazyIvanSocket);
        context.destroySocket(crazyIvanSocket);
        crazyIvanSocket = null;
      }
    } else if (serviceType == clymanType) {
      if (clymanSocket != null) {
        poller.unregister(clymanSocket);
        context.destroySocket(clymanSocket);
        clymanSocket = null;
      }
    }
  }

  // Reset the active socket for a service
  private void reset_socket(int serviceType) {
    // First, destroy the socket
    destroy_socket(serviceType);
    // Then, create a new socket
    if (serviceType == ivanType) {
      crazyIvanSocket = context.createSocket(ZMQ.REQ);
      poller.register(crazyIvanSocket, ZPoller.POLLIN);
    } else if (serviceType == clymanType) {
      clymanSocket = context.createSocket(ZMQ.REQ);
      poller.register(clymanSocket, ZPoller.POLLIN);
    }
  }

  /**
  * Destroy the DVS Manager, closing any open sockets.
  */
  @PreDestroy
  public void destroy() {
    destroy_socket(ivanType);
    destroy_socket(clymanType);
    context.destroy();
  }

  // Connect to the current socket for a service
  private void connect_to_socket(int serviceType) {
    // Pull the URL String
    String uriString = null;
    if (serviceType == ivanType) {
      uriString = crazyIvanInstance.getUri().toString();
    } else if (serviceType == clymanType) {
      uriString = clymanInstance.getUri().toString();
    }
    // Parse the URL String
    int portSeperatorIndex = uriString.lastIndexOf(":");
    String hostName = uriString.substring(7, portSeperatorIndex);
    String portStr =
        uriString.substring(portSeperatorIndex + 1, uriString.length());
    String zmqAddr = String.format("tcp://%s:%s", hostName, portStr);
    logger.info("Connecting to server: " + zmqAddr);
    // Connect to the service
    get_socket(serviceType).connect(zmqAddr);
  }

  // Report a failure of a service
  private void report_failure(int serviceType) {
    // Is the current host already on the greylist?
    Object cacheResp = null;
    if (serviceType == clymanType) {
      cacheResp =
          clymanGreylist.getIfPresent(clymanInstance.getUri().toString());
    } else if (serviceType == ivanType) {
      cacheResp =
          ivanGreylist.getIfPresent(crazyIvanInstance.getUri().toString());
    }
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      // Eliminate the socket
      destroy_socket(serviceType);
      if (cacheResp != null) {
        // We have found an entry in the greylist, add the host to the blacklist
        if (serviceType == clymanType) {
          cacheResp = clymanBlacklist.get(clymanInstance.getUri().toString());
        } else if (serviceType == ivanType) {
          cacheResp = ivanBlacklist.get(crazyIvanInstance.getUri().toString());
        }
      } else {
        // We have no entry in the greylist, add the hostname to the greylist
        if (serviceType == clymanType) {
          cacheResp = clymanGreylist.get(clymanInstance.getUri().toString());
        } else if (serviceType == ivanType) {
          cacheResp = ivanGreylist.get(crazyIvanInstance.getUri().toString());
        }
      }
    } catch (Exception e) {
      logger.error("Error Resetting Crazy Ivan connection");
      logger.error(e.getMessage());
    }
  }

  // Setup method to find and connect to an instance of Crazy Ivan
  private void find_service(int serviceType) {
    logger.info("Finding a new Crazy Ivan instance");
    // Find an instance of CrazyIvan
    List<ServiceInstance> serviceInstances = null;
    if (serviceType == ivanType) {
      serviceInstances = consulClient.getInstances("Ivan");
    } else if (serviceType == clymanType) {
      serviceInstances = consulClient.getInstances("Clyman");
    }
    if (serviceInstances != null) {
      //Log if we find no service instances
      if (serviceInstances.size() == 0) {
        logger.error("No Service instances found");
      }
      // Find a service Instance not on the blacklist
      for (int i = 0; i < serviceInstances.size(); i++) {
        Object cacheResp = null;
        // Pull the service instance from the list, and the value from the greylist
        if (serviceType == ivanType) {
          crazyIvanInstance = serviceInstances.get(i);
          logger.debug("Found Crazy Ivan Instance: "
              + crazyIvanInstance.getUri().toString());
          cacheResp =
              ivanBlacklist.getIfPresent(crazyIvanInstance.getUri().toString());
        } else if (serviceType == clymanType) {
          clymanInstance = serviceInstances.get(i);
          logger.debug("Found CLyman Instance: "
              + clymanInstance.getUri().toString());
          cacheResp =
              clymanBlacklist.getIfPresent(clymanInstance.getUri().toString());
        }
        // We can go ahead and connect to the instance as long as it isn't
        // on the blacklist
        if (cacheResp == null) {
          try {
            // Crazy Ivan ZMQ Context & Socket
            // Close any existing socket before creating a new one
            reset_socket(serviceType);

            // Connect to the new socket
            // First we need to format the address from Consul.  We also assume
            // tcp Communications between this class and Crazy Ivan
            connect_to_socket(serviceType);
          } catch (Exception e) {
            logger.error("Error connecting to Crazy Ivan instance");
            logger.error(e.getMessage());
            report_failure(serviceType);
          }
          // Exit the loop
          break;
        } else {
          logger.error("Returned host found in blacklist");
          if (serviceType == ivanType) {
            crazyIvanInstance = null;
          } else if (serviceType == clymanType) {
            clymanInstance = null;
          }
        }
      }
    } else {
      logger.error("Unable to find Service instance");
    }
  }

  private String send_msg_recursive(
      String msg, int timeout, int retries, int serviceType) {
    logger.info("Attempting to send message to Crazy Ivan");
    // Find a Crazy Ivan instance, if necessary
    if (!is_socket_active(serviceType)) {
      find_service(serviceType);
    }
    // Response Processing
    int retriesLeft = retries;
    while (retriesLeft > 0
        && !Thread.currentThread().isInterrupted()
        && is_socket_active(serviceType)) {
      //  We send a request, then we work to get a reply
      get_socket(serviceType).send(msg.getBytes(ZMQ.CHARSET), 0);

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
        if (poller.isReadable(get_socket(serviceType))) {
          //  We got a reply from the server
          return get_socket(serviceType).recvStr();
        } else if (--retriesLeft == 0) {
          logger.error("Reporting Crazy Ivan Failure");
          report_failure(serviceType);
          // Keep trying to send the message until we succeed or run out of
          // Crazy Ivan instances
          return send_msg_recursive(msg, timeout, retries, serviceType);
        } else {
          logger.warn("No response from server, retrying");
          //  Old socket is confused; close it and open a new one
          reset_socket(serviceType);
          connect_to_socket(serviceType);
          //  Send request again, on new socket
          get_socket(serviceType).send(msg.getBytes(ZMQ.CHARSET), 0);
        }
        if (rc < 0) {
          break;
        }
      }
    }
    return null;
  }

  /**
  * Send a message to Crazy Ivan, return the response.
  */
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
      return send_msg_recursive(msg, timeout, retries, ivanType);
    } catch (Exception e) {
      logger.error("Error Sending message to Crazy Ivan: ", e);
    } finally {
      // Release the mutex
      crazyIvanMutex.release();
    }
    return null;
  }

  /**
  * Send a message to CLyman, return the response.
  */
  public String send_to_clyman(String msg, int timeout, int retries) {
    // Grab the mutex so we ensure we operate atomically on connections
    try {
      clymanMutex.acquire();
    } catch (InterruptedException e) {
      logger.error("Error Establishing Mutex Lock on ZMQ Socket");
      logger.error(e.getMessage());
    }
    // Actually try to send the message
    try {
      return send_msg_recursive(msg, timeout, retries, clymanType);
    } catch (Exception e) {
      logger.error("Error Sending message to CLyman: ", e);
    } finally {
      // Release the mutex
      clymanMutex.release();
    }
    return null;
  }

}
