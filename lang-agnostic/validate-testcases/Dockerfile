FROM java:8-jre

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

ADD ./target/validate-testcases-1.0.0-jar-with-dependencies.jar /app/validate-testcases.jar

WORKDIR /files

ENTRYPOINT ["java", "-jar", "/app/validate-testcases.jar"]
CMD ["--base_dir=./"]
