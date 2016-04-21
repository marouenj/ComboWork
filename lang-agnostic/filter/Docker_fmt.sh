#!/bin/bash

docker run \
       --rm \
       --name fmt \
       -v "$(pwd)/filter.py:/files/filter.py" \
combowork/filter autopep8 --in-place --aggressive --aggressive filter.py
