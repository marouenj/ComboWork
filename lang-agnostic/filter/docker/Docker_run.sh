#!/bin/bash

docker run \
       --rm \
       --name filter \
       -v "$(pwd)/resources/combined:/files/combined" \
       -v "$(pwd)/resources/filters:/files/filters" \
       -v "$(pwd)/resources/filtered:/files/filtered" \
combowork/filter
