#!/bin/bash

docker run \
       --rm \
       --name maven \
       --net host \
       -v "$(pwd):/files" \
       -v "${HOME}/.m2:/root/.m2" \
marouenj/maven $1
