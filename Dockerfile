FROM gradle:6.5.1-jdk14 AS build
COPY --chown=gradle:gradle . /polly
WORKDIR /polly
RUN gradle shadowJar --no-daemon

FROM openjdk:11.0.8-jre-slim
RUN mkdir /config/
COPY --from=build /polly/build/libs/Polly.jar /

ENTRYPOINT ["java", "-jar", "/Polly.jar"]