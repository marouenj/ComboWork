#!/bin/bash

docker run \
       --rm \
       --name autopep8 \
       -v $(pwd):/files \
marouenj/autopep8
