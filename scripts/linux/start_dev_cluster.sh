#!/bin/bash

printf "Setting fixed IP Address:\n"
export NETWORK_INTERFACE_NAME=$(route | grep '^default' | grep -o '[^ ]*$')
export NETWORK_INTERFACE_ADDRESS=$(ip addr show $NETWORK_INTERFACE_NAME | grep -Po 'inet \K[\d.]+')
printf $NETWORK_INTERFACE_NAME
printf "\n"
printf $NETWORK_INTERFACE_ADDRESS

printf "\nStarting Cluster\n"
docker-compose -f ../../docker-compose.yml up
