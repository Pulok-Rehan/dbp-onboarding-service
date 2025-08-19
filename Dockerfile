# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
ARG JAR_FILE=target/dbp-onboarding-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Create upload directory inside container (optional)
RUN mkdir -p /uploads

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-Dsun.net.inetaddr.ttl=0", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
