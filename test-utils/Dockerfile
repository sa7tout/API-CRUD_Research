FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/test-utils-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]