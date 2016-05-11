#!/bin/bash

docker run \
       --rm \
       --name go \
       --net none \
       -v $(pwd)/bitutil:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/bitutil \
       -v $(pwd)/combiner:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/combiner \
golang:1.6 \
go build ./...
