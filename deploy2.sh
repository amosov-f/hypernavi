#!/usr/bin/env bash

docker build -t amosov/hypernavi .
docker tag $(docker images -q amosov/hypernavi) amosov/hypernavi
docker push amosov/hypernavi
docker stop hypernavi
docker rm hypernavi
docker run --name hypernavi -p 7340:80 -v /root/log:/root/log -v /root/hypernavi-data/data/:/root/hypernavi-data/data/ -d amosov/hypernavi

# mongodb
# docker run --name mongo -p 27017:27017 -d mongo
# docker stop mongo && docker rm mongo
# local
# sudo mongod --fork --logpath /var/log/mongod.log

# nginx
# docker run --name nginx -p 80:80 -p 443:443 -v /root/hypernavi-data/nginx.conf:/etc/nginx/conf.d/default.conf -v /etc/nginx/ssl:/etc/nginx/ssl -v /root/hypernavi-data/data/img:/usr/share/nginx/img -v /root/hypernavi-data/data/thumb:/usr/share/nginx/thumb -d nginx
# docker stop nginx && docker rm nginx

# https
# sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/nginx/ssl/nginx.key -out /etc/nginx/ssl/nginx.crt