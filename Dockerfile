FROM maven:3.8.6-amazoncorretto-17
WORKDIR /stockify_app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline

EXPOSE 8080
CMD ["mvn", "spring-boot:run"]