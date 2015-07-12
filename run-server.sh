#!/usr/bin/env bash

# ssh hypernavi@hypernavi.cloudapp.net
# sudo sh /hol/arkanavt/hypernavi/run-server.sh

git pull
mvn3 clean install -Dandroid.sdk.path=/opt/android-sdk-linux
fuser -KILL -k -n tcp 80
nohup java -jar server/target/hypernavi-server-jar-with-dependencies.jar -port 80 -cfg /common.properties /test.properties 2>> /dev/null >> /dev/null &