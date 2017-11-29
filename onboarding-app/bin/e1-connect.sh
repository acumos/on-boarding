DOCKERID=$(docker ps -a | grep e1-backend | awk '{print $1}')
echo "Connecting to $DOCKERID"
docker exec -it "$DOCKERID" bash

