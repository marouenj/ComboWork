#!/bin/bash

docker run \
       --rm \
       --name marshal4j \
       -v $(pwd)/src/test/resources:/files \
       combowork/marshal4j:latest \
       --vars="vars.json" --vals="vars.json.vals" --out="vars.json.marshal4j"
