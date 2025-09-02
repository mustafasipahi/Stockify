FROM maven:3.8.6-amazoncorretto-17 AS maven_build
WORKDIR /stockify_app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM amazoncorretto:17-alpine
WORKDIR /stockify_app

COPY --from=maven_build /stockify_app/target/Stockify-1.0-SNAPSHOT.jar stockify.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "stockify.jar"]