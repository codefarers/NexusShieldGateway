#Build the application
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

#Copy gradle/wrapper and build first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

#Copy source code
COPY src src

#Build the fat jar(skipping tests to speed up container build)
RUN ./gradlew bootJar -x test

#Run the application
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

#Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

#Expose the application port
EXPOSE 8080

#Run the Springboot application
ENTRYPOINT ["java", "-jar", "app.jar"]