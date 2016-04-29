FROM python:3.5

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

COPY *.py /app/

WORKDIR /files

CMD ["python", "/app/filter.py", "--combined=combined", "--filters=filters", "--filtered=filtered"]
