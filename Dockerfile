FROM openjdk:14-jdk-alpine

WORKDIR /app

RUN apk add maven
COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
RUN ls -l target

ENTRYPOINT ["java", "-jar", "target/gateway-0.0.1-SNAPSHOT.jar"]