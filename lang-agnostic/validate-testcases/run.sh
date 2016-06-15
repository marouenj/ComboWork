#!/bin/bash

docker run \
       -it \
       --rm \
       --name validate-testcases \
       --net none \
       -v /tmp/combined:/files \
combowork/validate-testcases:latest

echo $?
