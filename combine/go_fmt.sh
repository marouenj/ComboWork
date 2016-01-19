#!/bin/bash

docker run --rm --name go -v $(pwd):/go golang:1.5 sh -c "go fmt bitutil; go fmt testsuite; go fmt combine;"
