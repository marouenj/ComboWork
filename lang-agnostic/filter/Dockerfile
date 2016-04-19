FROM python:3.5

MAINTAINER marouenj

RUN mkdir /app
RUN mkdir /files

COPY *.py /app/

WORKDIR /files

ENTRYPOINT ["python", "/app/filter.py"]
CMD ["--combined=combined", "--filters=filters", "--filtered=filtered"]
