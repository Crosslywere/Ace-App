FROM openjdk:21-oracle
COPY ./target/*.jar app.jar
EXPOSE 8081
LABEL version="1.0.0-SNAPSHOT"
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
