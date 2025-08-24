FROM maven:3.8.6-amazoncorretto-17 AS maven_build
WORKDIR /stockify_app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM amazoncorretto:17-alpine
WORKDIR /stockify_app
COPY --from=maven_build /stockify_app/target/Stockify-1.0-SNAPSHOT.jar stockify.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","stockify.jar"]