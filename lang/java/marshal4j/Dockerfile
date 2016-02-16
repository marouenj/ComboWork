FROM java:8-jre

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

ADD ./target/marshal4j-jar-with-dependencies.jar /app/marshal4j.jar

WORKDIR /files

ENTRYPOINT ["java", "-jar", "/app/marshal4j.jar"]
CMD ["-in="./"]
