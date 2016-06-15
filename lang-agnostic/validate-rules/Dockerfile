FROM java:8-jre

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

ADD ./target/validate-rules-1.0.0-jar-with-dependencies.jar /app/validate-rules.jar

WORKDIR /files

ENTRYPOINT ["java", "-jar", "/app/validate-rules.jar"]
CMD ["--base_dir=./"]
