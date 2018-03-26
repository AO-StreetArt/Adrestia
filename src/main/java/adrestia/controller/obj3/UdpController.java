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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
* A Service which exposes Object Overwrites on a UDP port.
* Implements the Application Runner interface so that the port
* gets opened after config values and beans have been autowired.
*/
@Component
public class UdpController implements ApplicationRunner {
  @Value("${server.udp.port}")
  private int port;
  private DatagramSocket socket;

  // Object Controller Logger
  private static final Logger logger =
      LogManager.getLogger("adrestia.UdpController");

  // DAO Object allowing access to object data
  @Autowired
  ObjectDao objData;

  /**
  * Start listening on the UDP Socket.
  */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    logger.info("Starting UDP API on port" + this.port);
    // Define our runnable to execute on the background thread
    int updPort = this.port;
    Runnable r = new Runnable() {
      @Override
      public void run() {
        try {
          socket = new DatagramSocket(updPort);
        } catch (Exception e) {
          logger.error("Error opening UDP Socket: ", e);
          return;
        }
        ObjectMapper mapper = new ObjectMapper();
        while (true) {
          byte[] buf = new byte[512];
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          try {
            socket.receive(packet);
          } catch (Exception e) {
            logger.error("Error reading data from UDP Socket: ", e);
            return;
          }
          String received
              = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
          logger.info("Received UDP Request for Object Overwrite");
          // Send the update
          ObjectDocument inpObject;
          try {
            inpObject = mapper.readValue(received, ObjectDocument.class);
          } catch (Exception e) {
            logger.error("Error parsing data from UDP Socket: ", e);
            return;
          }
          ObjectList clymanResponse = objData.overwrite(inpObject);
          // If we have a successful response, then we pull the first value
          if (clymanResponse.getNumRecords() <= 0
              || clymanResponse.getErrorCode() != 100) {
            logger.debug("Failure Registered.  Clyman Response Error Code and Length:");
            logger.debug(clymanResponse.getNumRecords());
            logger.debug(clymanResponse.getErrorCode());
          }
        }
      }
    };

    // Start the thread to listen on the socket
    new Thread(r).start();
  }

  /**
  * Default empty UdpController constructor.
  */
  public UdpController() {
    super();
  }
}
