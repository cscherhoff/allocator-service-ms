FROM openjdk:12
ADD target/allocator-service-ms-0.0.1-SNAPSHOT.jar allocator-service-ms-0.0.1-SNAPSHOT.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "allocator-service-ms-0.0.1-SNAPSHOT.jar"]
LABEL prune=true
