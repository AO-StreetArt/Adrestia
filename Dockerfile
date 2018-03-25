################################################################

# Dockerfile to build Adrestia Container Images

################################################################


FROM openjdk:8-jdk-alpine
MAINTAINER Alex Barry
VOLUME /tmp
ADD target/adrestia-0.1.0.jar app.jar
ENV JAVA_OPTS=""
ENV SPRING_APPLICATION_JSON='{"server":{"port":"5885","ivan":{"retries":"3","timeout":"5000"},"zmq":{"redlist":{"duration":"10"},"greylist":{"duration":"10"},"blacklist":{"duration":"10"}}, "udp": {"port": "5886"}},"management":{"port":"5885","address":"127.0.0.1"},"spring":{"application":{"name":"Adrestia"},"profiles":{"active":"dev"},"cloud":{"consul":{"discovery":{"preferIpAddress":"false"}}}}}'
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
