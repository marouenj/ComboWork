#!/bin/bash

for FILE in filter.py filter_util.py filter_test.py;
do
docker run \
       --rm \
       --name fmt \
       -v "$(pwd):/files" \
combowork/filter autopep8 --in-place --aggressive --aggressive ${FILE}
done
