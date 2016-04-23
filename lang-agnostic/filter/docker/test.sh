#!/bin/bash

docker run \
       -it \
       --rm \
       --name filter \
       -v "$(pwd):/usr/src/myapp" \
       -w /usr/src/myapp \
python:3.5 python filter_test.py
