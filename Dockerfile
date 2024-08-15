FROM openjdk:17-jdk-slim
EXPOSE 8081
ADD target/prometheus-actuator-0.0.1-SNAPSHOT.jar spring-monitoring-demo.jar
ENTRYPOINT ["java","-jar","/spring-monitoring-demo.jar"]