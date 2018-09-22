################################################################

# Dockerfile to build Adrestia Container Images

################################################################


FROM openjdk:8-jdk-alpine
MAINTAINER Alex Barry
VOLUME /tmp
ADD build/libs/adrestia-0.2.0.jar app.jar
ENV JAVA_OPTS=""
ENV SPRING_APPLICATION_JSON='{"server":{"port":8080,"mongo":{"host":"localhost","port":27017}},"cache":{"max_scenes":500},"service":{"static":{"cluster":"test"},"refresh":{"interval":2500},"discovery":{"active":"false"},"ivan":{"name":"CrazyIvan","host":"crazyivan","port":8766},"clyman":{"name":"Clyman","host":"clyman","port":8768},"avc":{"name":"Avc","host":"avc","port":5635}},"management":{"port":5635,"address":"127.0.0.1"},"spring":{"application":{"name":"Adrestia"},"profiles":{"active":"dev"},"cloud":{"consul":{"discovery":{"host":"registry","port":8500,"preferIpAdress":"false"}}}},"zuul":{"routes":{"scene":{"url":"http://localhost:8090"},"object":{"url":"http://localhost:8090"},"property":{"url":"http://localhost:8090"},"asset":{"url":"http://localhost:8090"}}},"logging":{"level":{"org":{"springframework":"INFO","apache":{"http":"INFO"}}}},"ribbon":{"eureka":{"enabled":false}}}'
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
