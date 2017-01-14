#!/usr/bin/env bash

docker build -t amosov/hypernavi .
docker tag $(docker images -q amosov/hypernavi) amosov/hypernavi
docker push amosov/hypernavi
docker stop hypernavi && docker rm hypernavi
docker run --name hypernavi -p 7340:80 -v /root/log:/root/log -v /root/hypernavi-data/data/:/root/hypernavi-data/data/ -d amosov/hypernavi

# mongodb
# docker run --name hypernavi-mongo -p 27017:27017 -d mongo --auth
# docker exec -it hypernavi-mongo mongo admin
# db.createUser({ user: 'root', pwd: '***', roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] });
# exit
# docker exec -it hypernavi-mongo mongo admin -u "root" -p "***"
# use hypernavi
# db.createUser({ user: 'hypernavi', pwd: '***', roles: [ { role: "dbOwner", db: "hypernavi" } ] });
# exit
# mongo -u "hypernavi" -p "***" localhost --authenticationDatabase "hypernavi"
# mongorestore --username hypernavi --password 12345 --authenticationDatabase hypernavi --port 27017 backup/dump --drop

# docker stop hypernavi-mongo && docker rm hypernavi-mongo
# local
# docker start  `docker ps -q -l`
# sudo mongod --fork --logpath /var/log/mongod.log

# nginx
# docker run --name nginx -p 80:80 -p 443:443 -v /root/hypernavi-data/nginx.conf:/etc/nginx/conf.d/default.conf -v /etc/nginx/ssl:/etc/nginx/ssl -v /root/hypernavi-data/data/img:/usr/share/nginx/img -v /root/hypernavi-data/data/thumb:/usr/share/nginx/thumb -d nginx
# docker stop nginx && docker rm nginx

# https
# sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/nginx/ssl/nginx.key -out /etc/nginx/ssl/nginx.crt