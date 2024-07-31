# FROM amazoncorretto:17-al2023-jdk

# RUN yum -y install wget
# RUN ARCH=$(uname -m) && \
#     if [ "$ARCH" = "x86_64" ]; then \
#         wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_x86_64 && \
#         chmod +x /usr/local/bin/dumb-init; \
#     elif [ "$ARCH" = "aarch64" ]; then \
#         wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_arm64.deb && \
#         chmod +x /usr/local/bin/dumb-init; \
#     else \
#         echo "Unsupported architecture: $ARCH"; exit 1; \
#     fi

# RUN chmod +x /usr/local/bin/dumb-init

# WORKDIR /usr/app

# COPY ./entrypoint.sh ./

# ENTRYPOINT [ "/usr/local/bin/dumb-init", "--" ]

# CMD [ "/bin/sh", "./entrypoint.sh", "deploy" ]

# Step 1: Build the Spring Boot application using a Gradle image
FROM gradle:7.6.0-jdk17 AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy the source code
COPY src ./src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build

# Step 2: Create the final image to run the Spring Boot application
FROM amazoncorretto:17-al2023-jdk

# Set the working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]