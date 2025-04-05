# 1st stage: Build the Java app
FROM maven:3.9.6-amazoncorretto-17 AS maven-builder

WORKDIR /build

# Copy everything (including submodules)
COPY . .

# Step 1: Build submodule
RUN cd allocator-service && mvn clean install -DskipTests && cd ..

# Step 2: Build main project
RUN mvn clean package -DskipTests

# Stage 2: Custom JRE
FROM amazoncorretto:17.0.9-alpine AS jdk-builder
RUN apk add --no-cache binutils

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

# Stage 3: Final image
FROM alpine:latest AS final

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy JRE from the base image
COPY --from=jdk-builder /customjre $JAVA_HOME

# Add app user
ARG APPLICATION_USER=appuser
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

# Configure working directory
RUN mkdir /app && chown -R $APPLICATION_USER /app

USER 1000
WORKDIR /app

COPY --from=maven-builder /build/target/*.jar /app/app.jar

EXPOSE 8068
ENTRYPOINT [ "/jre/bin/java", "-jar", "/app/app.jar" ]
