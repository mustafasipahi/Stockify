FROM maven:3.8.6-amazoncorretto-17

WORKDIR /stockify_app

COPY pom.xml .
RUN mvn dependency:go-offline -B

ENV SPRING_DEVTOOLS_RESTART_ENABLED=true
ENV SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true

EXPOSE 8080
EXPOSE 35729

CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=\"-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0\""]