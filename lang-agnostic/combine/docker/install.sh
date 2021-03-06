#!/bin/bash

docker run \
       --rm \
       --name go \
       --net none \
       -v $(pwd)/combine.go:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/combine.go \
       -v $(pwd)/bitutil:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/bitutil \
       -v $(pwd)/combiner:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/combiner \
       -v $(pwd):/go/bin \
golang:1.6 \
go install github.com/marouenj/ComboWork/lang-agnostic/combine
