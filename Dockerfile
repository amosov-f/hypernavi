FROM java:8-jre
FROM nginx

EXPOSE 80

COPY server/target/hypernavi-server-jar-with-dependencies.jar /root/hypernavi-server-jar-with-dependencies.jar
COPY nginx.conf /etc/nginx/nginx.conf

ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/root/hypernavi-server-jar-with-dependencies.jar", "-port", "7340", "-cfg", "classpath:/properties/common.properties", "classpath:/properties/testing.properties", "-logcfg", "classpath:/logging/log4j-dev.xml", "-logdir", "/root/log", "-bot"]