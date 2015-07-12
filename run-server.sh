#!/usr/bin/env bash

git pull
mvn3 clean install -Dandroid.sdk.path=/opt/android-sdk-linux
fuser -KILL -k -n tcp 80
java -jar server/target/hypernavi-server.jar -port 80 -cfg /common.properties