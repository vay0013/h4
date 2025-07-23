FROM maven as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/h4-0.0.1.jar app.jar
CMD ["java", "-jar", "app.jar"] 