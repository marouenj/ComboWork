#!/bin/bash

if [[ $1 == "stop"]];
then
  docker rm -f combine
fi

if [[ $1 == "rm" ]];
then
  docker rm combine
fi

docker run --name combine -v $(pwd):/files combowork/combine
