FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/graphql-api-1.0-SNAPSHOT.jar app.jar
EXPOSE 9081
ENTRYPOINT ["java","-jar","app.jar"]