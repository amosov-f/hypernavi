FROM java:8-jre
EXPOSE 8080
ADD server/target/hypernavi-server-jar-with-dependencies.jar /hypernavi-server-jar-with-dependencies.jar
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/hypernavi-server-jar-with-dependencies.jar", "-port", "8080", "-cfg", "classpath:/common.properties", "classpath:/testing.properties", "-logcfg", "classpath:/log4j-dev.xml", "-logdir", "/root/log", "-bot"]