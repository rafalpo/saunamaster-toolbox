FROM docker.io/library/maven:3.9-eclipse-temurin-17 AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

FROM docker.io/library/maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app/pom.xml .
COPY src ./src
RUN mvn -DskipTests -Pdocker clean package

FROM docker.io/library/eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/saunamaster-toolbox.jar ./backend.jar
EXPOSE 8080
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java", "-jar", "backend.jar"]