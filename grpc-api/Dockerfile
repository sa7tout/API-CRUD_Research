FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/grpc-api-1.0-SNAPSHOT.jar app.jar
EXPOSE 9083 9093
ENTRYPOINT ["java","-jar","app.jar"]