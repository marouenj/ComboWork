#!/bin/bash

docker run --rm --name go -v $(pwd):/go golang:1.5 go test bitutil