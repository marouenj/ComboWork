#!/bin/bash

docker run --rm --name maven -v "$PWD:/files" -v "$PWD/.m2:/root/.m2" marouenj/maven $1
