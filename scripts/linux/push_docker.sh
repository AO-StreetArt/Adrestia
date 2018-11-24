#!/bin/bash

BRANCH_NAME=$4

if [ "$BRANCH_NAME" == "v2" ]; then
  docker push aostreetart/adrestia:v2
fi
