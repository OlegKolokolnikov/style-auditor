FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package spring-boot:repackage \
    && cp target/*.jar /tmp/app.jar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /tmp/app.jar ./app.jar

ENV SERVER_PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]