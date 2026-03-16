FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder

WORKDIR /project

COPY pom.xml /project/

RUN mvn dependency:go-offline

COPY /src /project/src

RUN mvn -B package -DskipTests



FROM eclipse-temurin:21-jre-alpine

WORKDIR /usr/src/app/

COPY --from=builder /project/target/*.jar /usr/src/app/app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]