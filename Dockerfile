FROM gradle:9.8.1-jdk17 AS build
COPY --chown=gradle:gradle . /polly
WORKDIR /polly
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17.0.20_8-jre-alpine
RUN mkdir /config/
COPY --from=build /polly/build/libs/Polly.jar /

ENTRYPOINT ["java", "-jar", "/Polly.jar"]
