# Step 1: Build the application
FROM gradle:jdk21 AS build
WORKDIR /khoa/jobhunter-BE
COPY --chown=gradle:gradle . .

# Step 2: Build the JAR (Skip tests for faster build)
RUN gradle clean build -x test --no-daemon

# Step 3: Run the application
FROM openjdk:25-slim
WORKDIR /app
EXPOSE 8080

# Install netcat
RUN apt-get update && apt-get install -y netcat-openbsd

# Copy the JAR and entrypoint.sh
COPY --from=build /khoa/jobhunter-BE/build/libs/*.jar /app/jobhunter-application.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
