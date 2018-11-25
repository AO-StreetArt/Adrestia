#!/bin/bash

BRANCH_NAME=$TRAVIS_BRANCH
echo "Branch Name: $BRANCH_NAME"

if [ "$BRANCH_NAME" == "v2" ]; then
  docker push aostreetart/adrestia:v2
fi
