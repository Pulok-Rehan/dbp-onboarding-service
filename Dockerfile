# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY dbp-onboarding-service-0.0.1-SNAPSHOT.jar app.jar
COPY application.properties application.properties


# Create upload directory inside container (optional)
RUN mkdir -p /uploads

# Expose the port your Spring Boot app runs on
EXPOSE 9092

# Run the JAR file with the --add-opens argument
ENTRYPOINT ["java", "--add-opens", "java.base/java.io=ALL-UNNAMED", "-jar", "app.jar", "--spring.config.location=file:/app/application.properties"]
