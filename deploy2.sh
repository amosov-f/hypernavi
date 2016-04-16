#!/usr/bin/env bash

# moving data
rsync -a data /root

IMAGES=$(docker ps -a -q --filter ancestor=amosov/hypernavi --format="{{.ID}}")
docker build -t amosov/hypernavi .
docker tag $(docker images -q amosov/hypernavi) amosov/hypernavi
docker push amosov/hypernavi
docker rm $(docker stop $IMAGES)
docker run -p 7340:80 -d amosov/hypernavi

# mongodb
# docker run -p 27017:27017 -v /root/hypernavi-data/data/db:/data/db -d mongo
# docker run -p 80:80 -v /root/hypernavi-data/nginx.conf:/etc/nginx/nginx.conf:ro -v /root/hypernavi-data/img:/root/data/img -d nginx