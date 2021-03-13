FROM maven:3.6.3-jdk-11

WORKDIR /server

ADD . /server

RUN mvn -B package --file pom.xml

CMD ["java", "-jar", "target/server-1.0-dev.jar"]

EXPOSE 8018