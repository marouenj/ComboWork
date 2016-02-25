#!/bin/bash

docker run \
       --rm \
       --name combine \
       -v $(pwd)/src/test/resources:/files \
combowork/combine

docker run \
       --rm \
       --name maven \
       -v "$PWD:/files" \
       -v "$PWD/.m2:/root/.m2" \
marouenj/maven $1
