#!/bin/bash

cd `dirpath $0/`..
git daemon --verbose --export-all --base-path=.
docker run -d --name jenkins -p 8080:8080 -p 50000:50000 jenkins/jenkins:lts
docker run -d  -p 8081:8081 --name nexus sonatype/nexus

