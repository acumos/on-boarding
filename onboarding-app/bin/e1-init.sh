#!/bin/bash

# script to start a java instance for e1 backend server

BIN_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
RUN_DIR=$( cd "$( dirname "$BIN_DIR" )" && pwd ) 
echo "RUN: $RUN_DIR"
IMAGE_NAME=e1-backend
DOCKER_SRC=openjdk

# confirm we have docker image
docker pull $DOCKER_SRC

if [ "$(docker ps -a | grep $IMAGE_NAME | grep -iv exited)" == "" ]; then
  echo "Attempting to docker RUN '$IMAGE_NAME'..."
  # start instance if not running
  docker run --net="host" -v $RUN_DIR:/e1 -v /var/run/docker.sock:/docker_sock -v /home/cognitaopr:/docker_host -it --name "$IMAGE_NAME" -d $DOCKER_SRC 
  # docker run -v $RUN_DIR:/e1 -it -p 58022:22 -p 58080:8080 -p 58400-58600:58400-58600 --name "$IMAGE_NAME" -d $DOCKER_SRC
else
  echo "Found running docker instance '$IMAGE_NAME'..."
fi

echo " --- Launching services --- "
sleep 2
docker exec $IMAGE_NAME /bin/bash /e1/bin/e1-update.sh &

