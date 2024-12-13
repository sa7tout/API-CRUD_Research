FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/rest-api-1.0-SNAPSHOT.jar app.jar
EXPOSE 9080
ENTRYPOINT ["java","-jar","app.jar"]