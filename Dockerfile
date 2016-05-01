FROM java:8-jre
EXPOSE 80
ADD server/target/hypernavi-server-jar-with-dependencies.jar /root/hypernavi-server-jar-with-dependencies.jar
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/root/hypernavi-server-jar-with-dependencies.jar", "-port", "80", "-cfg", "classpath:/properties/common.properties", "classpath:/properties/testing.properties", "-logcfg", "classpath:/logging/log4j-dev.xml", "-logdir", "/root/log", "-bot"]