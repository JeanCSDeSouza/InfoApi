FROM eclipse-temurin:17-jdk-alpine

ARG APPLICATION_USER=spring
# Create a user to run the application, don't run as root
RUN addgroup --system $APPLICATION_USER && adduser --system $APPLICATION_USER --ingroup $APPLICATION_USER

# Create the application directory
RUN mkdir /app && chown -R $APPLICATION_USER /app

# Set the user to run the application
USER $APPLICATION_USER

# Copy the jar file to the container
COPY --chown=$APPLICATION_USER:$APPLICATION_USER target/*.jar /app/app.jar

# Set the working directory
WORKDIR /app

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]