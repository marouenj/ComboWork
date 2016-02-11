FROM alpine:3.3

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

COPY ./bin/combine /app/combine
ENV PATH $PATH:/app

WORKDIR /files

ENTRYPOINT ["combine"]
CMD ["--in=./"]
