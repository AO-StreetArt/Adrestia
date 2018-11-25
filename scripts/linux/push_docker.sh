#!/bin/bash

BRANCH_NAME=$(git branch | grep \* | cut -d ' ' -f2)
echo "Branch Name: $BRANCH_NAME"

if [ "$BRANCH_NAME" == "v2" ]; then
  docker push aostreetart/adrestia:v2
fi
