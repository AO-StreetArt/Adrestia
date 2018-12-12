################################################################

# Dockerfile to build Adrestia Container Images

################################################################


FROM openjdk:8-jdk-alpine
MAINTAINER Alex Barry
VOLUME /tmp
ADD build/libs/adrestia-0.2.0.jar app.jar
ADD src/main/resources/bootstrap.properties bootstrap.properties
ADD src/main/resources/application.properties application.properties
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
