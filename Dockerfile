# Stage 1: Build the application with Maven
FROM maven:3.8.6-eclipse-temurin-17 as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven configuration file and the source code
COPY pom.xml /app/
COPY src /app/src/
COPY WebContent /app/WebContent/

# Build the application
RUN mvn clean package

# Stage 2: Create a new image with Tomcat
FROM tomcat:9.0.93-jdk17

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the previous stage
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose the Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
