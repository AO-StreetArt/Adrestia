#!/bin/bash

DOCKER_EMAIL=$1
DOCKER_USER=$2
DOCKER_PASS=$3
BRANCH_NAME=$4

if [ "$BRANCH_NAME" == "v2" ]; then
  docker push aostreetart/adrestia:v2
fi
