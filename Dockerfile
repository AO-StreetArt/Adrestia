################################################################

# Dockerfile to build Adrestia Container Images

################################################################


FROM openjdk:8-jdk-alpine
MAINTAINER Alex Barry
VOLUME /tmp
ADD build/libs/adrestia-0.2.0.jar app.jar
ADD src/main/webapp/WEB-INF/jsp src/main/webapp/WEB-INF/jsp
ADD src/main/webapp/css src/main/webapp/css
ADD src/main/resources src/main/resources
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
