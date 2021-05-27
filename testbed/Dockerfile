FROM openjdk:15-jdk-alpine
LABEL maintainer="Enigio <info@enigio.com>"
ADD target/testbed-0.0.1-SNAPSHOT.jar /opt/original/testbed.jar
CMD ["sh", "-c", "java $JAVA_OPT -jar /opt/original/testbed.jar"]
