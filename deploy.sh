#!/bin/bash

HOST=hypernavi.net

# moving jar
FILENAME=hypernavi-dev-`stat -c %Y server/target/hypernavi-server-jar-with-dependencies.jar`.jar
fuser -KILL -k -n tcp 80
rm -r ~/hypernavi-dev-*
mv -v server/target/hypernavi-server-jar-with-dependencies.jar ~/${FILENAME}

# moving data
mv -v data /root/data

# starting server
nohup java -jar ~/${FILENAME} -port 80 -cfg /common.properties /dev.properties -logcfg /log4j-dev.xml -logdir /root/log 2>> /dev/null >> /dev/null &

# checking server start
LOADED=-1
ATTEMPTS=3
while [ ${LOADED} -ne 0 ] && [ ${ATTEMPTS} -gt 0 ]; do
	sleep 30
	nc -z -w 1 ${HOST} 80
	LOADED=$?
	ATTEMPTS=$(( ATTEMPTS-1 ))
done

if [ ${LOADED} -ne 0 ]; then
	echo "Server is not responding!"
	echo "##teamcity[buildStatus status='FAILED' text='Failed do deploy on $HOST']"
	exit 1
else
	echo "Server is started"
	echo "##teamcity[buildStatus status='SUCCESS' text='Deployed on $HOST']"
fi