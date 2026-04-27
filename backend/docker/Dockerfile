FROM node:20-alpine AS frontend-builder

WORKDIR /frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder

WORKDIR /project

COPY backend/pom.xml /project/
RUN mvn dependency:go-offline

COPY backend/src /project/src

COPY --from=frontend-builder /frontend/build/client/ \
     /project/src/main/resources/static/new/

RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /usr/src/app/

COPY --from=builder /project/target/*.jar /usr/src/app/app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]