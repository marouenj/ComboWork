#!/bin/bash

TAG=latest

if [[ $1 != "" ]];
then
  TAG=$1
fi

docker build -t combowork/validate-rules:${TAG} .
