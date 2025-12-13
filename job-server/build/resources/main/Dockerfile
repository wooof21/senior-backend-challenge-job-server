
# to pull gralde image for build the project
FROM gradle:8.13-jdk21-corretto AS BUILDER

WORKDIR /gradle/app

COPY build.gradle .
COPY src ./src

# Build the JAR (skip tests for faster build)
RUN gradle clean build -x test


FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /usr/share/app

COPY --from=BUILDER /gradle/app/build/libs/*.jar assessment.jar

EXPOSE 8080

CMD [ "java", "-jar", "assessment.jar" ]