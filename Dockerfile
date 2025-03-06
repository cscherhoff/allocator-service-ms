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

# Main app image
FROM --platform=$TARGETPLATFORM alpine:latest

# Set up Java environment
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the custom JRE from the base image
COPY --from=corretto-jdk /customjre $JAVA_HOME

# Add an unprivileged user for security
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

# Configure working directory
RUN mkdir /app && \
    chown -R $APPLICATION_USER /app

USER 1000

# Copy application JAR
COPY --chown=1000:1000 target/allocator-service-ms-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app

# Expose application port
EXPOSE 8085

# Run the Java application
ENTRYPOINT [ "/jre/bin/java", "-jar", "/app/app.jar" ]
