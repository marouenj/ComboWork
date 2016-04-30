FROM python:3.5

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

COPY *.py /app/

WORKDIR /files

CMD ["python", "/app/interpolate.py", "--combined=combined", "--templates=templates", "--interpolated=interpolated"]
