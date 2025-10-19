# Stage 1: Build the application using gradle wrapper
FROM gradle:8.5.0-jdk17-alpine AS build
WORKDIR /home/gradle/src
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Grant execution rights to the gradlew script
RUN chmod +x ./gradlew

# Build the project
RUN ./gradlew build -x test --no-daemon

# Stage 2: Create the runtime image
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Expose the port the app runs on (default is 8080)
EXPOSE 8080

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
