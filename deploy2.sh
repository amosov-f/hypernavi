#!/usr/bin/env bash

IMAGES=$(docker ps -a -q --filter ancestor=amosov/hypernavi --format="{{.ID}}")
docker build -t amosov/hypernavi .
docker tag $(docker images -q amosov/hypernavi) amosov/hypernavi
docker push amosov/hypernavi
docker rm $(docker stop $IMAGES)
docker run -p 80:80 -d amosov/hypernavi