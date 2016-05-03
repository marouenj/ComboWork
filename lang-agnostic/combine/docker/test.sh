#!/bin/bash

docker run \
       --rm \
       --name go \
       -v $(pwd)/bitutil:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/bitutil \
       -v $(pwd)/testsuite:/go/src/github.com/marouenj/ComboWork/lang-agnostic/combine/testsuite \
golang:1.6 \
go test ./...
