FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /api
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /api/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]