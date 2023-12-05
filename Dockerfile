FROM eclipse-temurin:21-jdk-alpine

VOLUME /tmp

COPY application/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]