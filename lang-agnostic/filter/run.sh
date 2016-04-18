#!/bin/bash

docker run \
	--rm \
	--name filter \
        -v "/tmp/combined:/files/combined" \
        -v "/tmp/filters:/files/filters" \
        -v "/tmp/filtered:/files/filtered" \
combowork/filter
