# Base image to build a minimal JRE
FROM --platform=$TARGETPLATFORM amazoncorretto:17.0.9-alpine AS corretto-jdk

# Required for strip-debug to work
RUN apk add --no-cache binutils

# Build a small JRE optimized for arm64
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

# --- Build stage using Maven ---
FROM maven:3.9.6-amazoncorretto-17-alpine AS build

# Copy project files
WORKDIR /build
COPY pom.xml .
COPY src ./src

# Download dependencies and build the JAR
RUN mvn clean package -DskipTests

# --- Final stage with custom JRE ---
FROM --platform=$TARGETPLATFORM alpine:latest

# Set up Java environment
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the custom JRE from the base image
COPY --from=corretto-jdk /customjre $JAVA_HOME

# Add an unprivileged user for security
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

# Set up app directory and permissions
RUN mkdir /app && \
    chown -R $APPLICATION_USER /app

USER 1000
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /build/target/allocator-service-ms-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8085

# Run the Java application
ENTRYPOINT [ "/jre/bin/java", "-jar", "/app/app.jar" ]
