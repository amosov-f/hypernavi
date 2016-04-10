#!/usr/bin/env bash

docker build -t amosov/hypernavi .
docker rm $(docker stop $(docker ps -a -q --filter ancestor=amosov/hypernavi --format="{{.ID}}"))
docker run -p 80:80 -d amosov/hypernavi