# Build stage
FROM gradle:8-jdk21 AS build

WORKDIR /app

COPY . .

RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

# Installation de ImageMagick
RUN apt-get update \
    && apt-get install -y --no-install-recommends imagemagick \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]