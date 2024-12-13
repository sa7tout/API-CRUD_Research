FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/soap-api-1.0-SNAPSHOT.jar app.jar
EXPOSE 9082
ENTRYPOINT ["java","-jar","app.jar"]