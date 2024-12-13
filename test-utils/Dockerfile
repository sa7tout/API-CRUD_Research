FROM eclipse-temurin:17-jdk-focal

WORKDIR /app
COPY target/*.jar app.jar
COPY src/test/jmeter /app/jmeter
COPY src/test/ressources /app/resources

RUN apt-get update && apt-get install -y wget unzip \
    && wget https://downloads.apache.org/jmeter/binaries/apache-jmeter-5.6.3.zip \
    && unzip apache-jmeter-5.6.3.zip \
    && mv apache-jmeter-5.6.3/* /app/jmeter/ \
    && rm -rf apache-jmeter-5.6.3.zip \
    && chmod +x /app/jmeter/bin/jmeter \
    && mkdir -p /app/jmeter/lib/ext

ENV JMETER_HOME=/app/jmeter
ENV PATH=$JMETER_HOME/bin:$PATH

# Copy test utils JAR to JMeter extensions lib
COPY target/test-utils-1.0-SNAPSHOT.jar /app/jmeter/lib/ext/

ENTRYPOINT ["java","-jar","app.jar"]