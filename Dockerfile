# Use an official Gradle image as the build stage
FROM gradle:6.9.2-jdk8 AS build
WORKDIR /app
COPY . /app
RUN gradle build --no-daemon

# Use an official OpenJDK 8 runtime image
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
# Copy the .env file to the root of the container's filesystem
COPY .env /app/.env

# Run the application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]