# The basic CLyman Flow performs a series of basic checks on the CLyman API
# It walks a single object through a full flow, step by step, and validates
# all of the fields on the object at each step of the way.

import logging
import sys
import json
import copy
import requests
import socket
import time

# Basic Config
aesel_addr = "http://127.0.0.1:5885"
udp_ip = "127.0.0.1"
udp_port = 5886
log_file = 'logs/udpTest.log'
log_level = logging.DEBUG

# Test Scene Data
test_scene_data = {
    "key":"",
    "name":"basicFlowName",
    "latitude":124.0,
    "longitude":122.0,
    "distance":100.0,
    "region":"basicFlowRegion",
    "assets":["basicAsset"],
    "tags":["basicTag"]
}

# The transforms we will track throughout the flow
# These get calculated by hand, and validate the transformations applied
# Current implementation applies LHS Matrix multiplication of translation,
# then euler rotation, then scale, in that order.
# Final Result can be found using http://matrix.reshish.com/multCalculation.php
# Translation Matrix:
#   For a translation <x, y, z>, the corresponding matrix is:
#       [1.0, 0.0, 0.0, x.0,
#        0.0, 1.0, 0.0, y.0,
#        0.0, 0.0, 1.0, z.0,
#        0.0, 0.0, 0.0, 1.0]
# Rotation Matrix:
#   For a rotation of theta degrees about the vector <x, y, z>, the rotation
#   matrix can be found using https://www.andre-gaschler.com/rotationconverter/
# Scale Matrix:
#   For a scale <x, y, z>, the corresponding matrix is:
#       [x.0, 0.0, 0.0, 0.0,
#        0.0, y.0, 0.0, 0.0,
#        0.0, 0.0, z.0, 0.0,
#        0.0, 0.0, 0.0, 1.0]
test_transform = [1.0, 0.0, 0.0, 0.0,
                  0.0, 1.0, 0.0, 0.0,
                  0.0, 0.0, 1.0, 0.0,
                  0.0, 0.0, 0.0, 1.0]

updated_test_transform = [2.0, 0.0, 0.0, 1.0,
                          0.0, 2.0, 0.0, 1.0,
                          0.0, 0.0, 2.0, 1.0,
                          0.0, 0.0, 0.0, 1.0]

# Object data represented through each piece of the flow
test_data = {
  "key": "",
  "name": "basicTestObject",
  "type": "basicTestType",
  "subtype": "basicTestSubtype",
  "owner": "basicTestOwner",
  "scene": "basicFlowName",
  "translation": [0.0, 0.0, 0.0],
  "euler_rotation": [0.0, 0.0, 0.0],
  "scale": [1.0, 1.0, 1.0],
  "assets": ["basicTestAsset"]
}

updated_test_data = {
  "key": "",
  "name": "basicTestObject",
  "type": "Mesh",
  "subtype": "Cube",
  "owner": "Alex",
  "scene": "basicFlowName",
  "translation": [1.0, 1.0, 1.0],
  "euler_rotation": [0.0, 0.0, 0.0],
  "scale": [2.0, 2.0, 2.0],
  "assets": ["anotherAsset"]
}

def execute_udp_flow(sock, udp_ip, udp_port, aesel_addr, test_data,
                     test_transform, updated_test_data, updated_test_transform):
    # Send the UDP Message
    sock.sendto(bytes(json.dumps(updated_test_data), 'UTF-8'), (udp_ip, udp_port))
    time.sleep(1)
    # Validate that the update went through correctly
    r = requests.get(aesel_addr + '/v1/scene/' + updated_test_data['scene'] + '/object/' + updated_test_data['name'])
    assert(r.status_code == requests.codes.ok)
    parsed_json = r.json()
    if parsed_json is not None:
        for i in range(0,16):
            logging.debug("Validating Transform element: %s" % i)
            assert(parsed_json["transform"][i] - updated_test_transform[i] < 0.01)


# Execute the actual tests
def execute_main():
    # Grab the global pieces of data
    global test_data
    global test_transform
    global updated_test_data
    global updated_test_transform
    global log_file
    global log_level
    global udp_ip
    global udp_port
    global aesel_addr

    logging.basicConfig(filename=log_file, level=log_level)

    # Connect to UDP Socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    logging.debug("Connected to UDP Socket")

    # Create a Scene and Object through the HTTP API
    r = requests.post(aesel_addr + '/v1/scene/' + test_scene_data['name'], json=test_scene_data)
    assert(r.status_code == requests.codes.ok)
    response_json = r.json()
    test_scene_data['key'] = response_json['key']
    r = requests.post(aesel_addr + '/v1/scene/' + test_scene_data['name'] + "/object/" + test_data['name'], json=test_data)
    assert(r.status_code == requests.codes.ok)
    response_json = r.json()
    test_data['key'] = response_json['key']
    updated_test_data['key'] = response_json['key']

    # Execute each test with a deep copy of the data, so that it stays
    # independent through each test
    execute_udp_flow(sock, udp_ip, udp_port, aesel_addr,
                      copy.deepcopy(test_data),
                      copy.deepcopy(test_transform),
                      copy.deepcopy(updated_test_data),
                      copy.deepcopy(updated_test_transform))

if __name__ == "__main__":
    if len(sys.argv) > 1:
        execute_main(sys.argv[1])
    else:
        execute_main()
