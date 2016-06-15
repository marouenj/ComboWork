#!/bin/bash

GOAL=test

if [[ $1 != "" ]];
then
  GOAL=$1;
fi

docker run \
       -it \
       --rm \
       --name maven \
       --net host \
       -v "${PWD}":/files \
       -v "${HOME}/.m2:/root/.m2" \
       -w /files \
maven:3-jdk-8 \
mvn ${GOAL}
