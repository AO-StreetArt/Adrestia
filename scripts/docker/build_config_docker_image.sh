#!/bin/bash

BRANCH_NAME=$1

if [ "$BRANCH_NAME" == "master" ]; then
  docker build --no-cache --file scripts/docker/PopulateConsulDockerfile -t "aostreetart/populateaeselconfig:latest" .
  docker push aostreetart/populateaeselconfig:latest
fi
