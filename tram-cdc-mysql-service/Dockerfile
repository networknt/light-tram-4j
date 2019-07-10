FROM openjdk:11.0.3-slim
ADD /target/tram-cdc-mysql-service.jar server.jar
CMD ["/bin/sh","-c","java -Dlight-4j-config-dir=/config -Dlogback.configurationFile=/config/logback.xml -cp /server.jar com.networknt.server.Server"]
