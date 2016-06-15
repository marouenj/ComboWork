#!/bin/bash

docker run \
       -it \
       --rm \
       --name validate-rules \
       --net none \
       -v /tmp/rules:/files \
combowork/validate-rules:latest

echo $?
