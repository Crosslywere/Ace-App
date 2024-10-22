FROM maven:latest AS build
RUN mvn -B package -Pproduction -DskipTests

FROM openjdk:21-oracle
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
LABEL version="1.0-SNAPSHOT"
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
