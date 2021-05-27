FROM openjdk:15-jdk-alpine
LABEL maintainer="Enigio <info@enigio.com>"
ADD target/consumers-0.0.1-SNAPSHOT.jar /opt/original/consumer.jar
CMD ["sh", "-c", "java $JAVA_OPT -jar /opt/original/consumer.jar"]
