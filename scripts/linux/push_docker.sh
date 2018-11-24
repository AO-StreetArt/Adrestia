#!/bin/bash

BRANCH_NAME=$4
echo $BRANCH_NAME

if [ "$BRANCH_NAME" == "v2" ]; then
  docker push aostreetart/adrestia:v2
fi
