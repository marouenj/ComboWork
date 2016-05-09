#!/bin/bash

docker run \
       --rm \
       --name go \
       -v $(pwd)/combine.go:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/combine.go \
       -v $(pwd)/combine_test.go:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/combine_test.go \
       -v $(pwd)/bitutil:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/bitutil \
       -v $(pwd)/combiner:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/combiner \
golang:1.6 \
go test ./...
