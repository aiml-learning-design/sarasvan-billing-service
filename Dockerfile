FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY target/billing-service.jar billing-service.jar
ENTRYPOINT ["java","-jar","/billing-service.jar"]