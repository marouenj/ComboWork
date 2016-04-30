#!/bin/bash

docker run \
       --rm \
       --name interpolate \
       -v "$(pwd)/resources/combined:/files/combined" \
       -v "$(pwd)/resources/templates:/files/templates" \
       -v "$(pwd)/resources/interpolated:/files/interpolated" \
combowork/interpolate
