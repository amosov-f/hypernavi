#!/bin/bash

HOST=hypernavi.net
PORT=7340
JAR_PATH=server/target/hypernavi-server-jar-with-dependencies.jar
FAIL_MESSAGE="##teamcity[buildStatus status='FAILED' text='Failed do deploy on $HOST']"

# checking jar
if [ ! -f ${JAR_PATH} ]; then
    echo "Jar not found!"
    echo ${FAIL_MESSAGE}
    exit 1
fi

rsync -a data /root
# moving jar
FILENAME=hypernavi-dev-`stat -c %Y ${JAR_PATH}`.jar
fuser -KILL -k -n tcp ${PORT}
rm -r ~/hypernavi-dev-*
mv -v ${JAR_PATH} ~/${FILENAME}

# moving data
rsync -a data /root

# starting server
nohup java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar ~/${FILENAME} -port ${PORT} -cfg classpath:/common.properties classpath:/testing.properties -logcfg classpath:/log4j-dev.xml -logdir /root/log 2>> /dev/null >> /dev/null &

# reloading nginx
mv -v nginx.conf /etc/nginx/sites-available/hypernavi.net
service nginx reload

# checking server start
LOADED=-1
ATTEMPTS=3
while [ ${LOADED} -ne 0 ] && [ ${ATTEMPTS} -gt 0 ]; do
	sleep 30
	nc -z -w 1 ${HOST} ${PORT}
	LOADED=$?
	ATTEMPTS=$(( ATTEMPTS-1 ))
done

if [ ${LOADED} -ne 0 ]; then
	echo "Server is not responding!"
	echo ${FAIL_MESSAGE}
	exit 1
else
	echo "Server is started"
	echo "##teamcity[buildStatus status='SUCCESS' text='Deployed on $HOST']"
fi