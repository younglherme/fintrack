FROM gradle:8.14.3-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY gradlew ./

COPY src ./src

RUN gradle clean bootJar --no-daemon


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S fintrack && adduser -S fintrack -G fintrack

COPY --from=build /app/build/libs/*.jar app.jar

USER fintrack

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]