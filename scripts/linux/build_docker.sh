#!/bin/bash

DOCKER_EMAIL=$1
DOCKER_USER=$2
DOCKER_PASS=$3
BRANCH_NAME=$4

if [ $BRANCH_NAME == "master" ]; then
  docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
  gradle buildDocker -Ppush
fi
