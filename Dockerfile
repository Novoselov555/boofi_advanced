FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

COPY gradlew ./gradlew
COPY gradle ./gradle/
COPY build.gradle* ./
COPY settings.gradle* ./

RUN if [ -f ./gradlew ]; then chmod +x ./gradlew; fi

COPY src ./src

RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"] 