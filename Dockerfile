#BUILDER
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

COPY ./ ./

RUN gradle clean bootJar

#APP
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/app-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app-0.0.1-SNAPSHOT.jar"]